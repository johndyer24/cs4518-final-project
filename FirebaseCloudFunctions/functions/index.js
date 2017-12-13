// import required modules
const functions = require('firebase-functions');
const admin = require('firebase-admin'); // The Firebase Admin SDK to access the Firebase Realtime Database.
const _ = require('lodash');
const geolib = require('geolib');

// max distance in meters
const MAX_DISTANCE = 100;

// setup app
admin.initializeApp(functions.config().firebase);
const db = admin.database();

/**
 * Function that pairs users who have requested a chat
 * Currently just pairs users based on request time
 */
exports.pairUsers = functions.database.ref('chatRequests/{reqID}').onCreate((event) => {

  console.log('PAIR USERS: pairUsers is starting');

  return db.ref('chatRequests').once('value').then((snapshot) => {
    // get object containing list of request objects
    let requests = snapshot.val();

    // add key of object as property, so that it isn't lost when converting object to array
    for (let key in requests) {
      requests[key].requestID = key;
    }

    // Use Lodash to create array of requests sorted by value of 'time'
    requests = _.orderBy(requests, 'time', 'asc');

    let updates = {};
    // can only pair users if there are two or more user requests
    if (requests.length >= 2) {
      console.log('PAIR USERS: pairing users');

      // keep track of which users have been paired
      let pairedUserIndices = [];

      // iterate over requests and pair users
      for (let i = 0; i < requests.length - 1; i += 1) {
        // if user i was paired, continue to next user
        if (pairedUserIndices.includes(i)) {
          continue;
        }

        for (let j = i+1; j < requests.length; j += 1) {
          // if user j was paired, continue to next user
          if (pairedUserIndices.includes(j)) {
            continue;
          }

          // pair users if they are within MAX_DISTANCE distance
          // and have at least 1 common interest
          // and aren't in another chat together
          if (withinMaxDistance(requests[i].latitude, requests[i].longitude,
            requests[j].latitude, requests[j].longitude)
            && commonInterests(requests[i].interests, requests[j].interests)
            && notPreviouslyPaired(requests[i].hasChatsWith, requests[j].userID)) {
              // delete chat requests
              updates['chatRequests/' + requests[i].requestID] = null;
              updates['chatRequests/' + requests[j].requestID] = null;

              // create new chat
              let newChatID = db.ref('chats').push().key;
              updates['chats/' + newChatID + '/startTime'] = admin.database.ServerValue.TIMESTAMP;
              updates['chats/' + newChatID + '/user1'] = requests[i].userID;
              updates['chats/' + newChatID + '/user2'] = requests[j].userID;
              updates['chats/' + newChatID + '/user1DisplayName'] = requests[i].displayName;
              updates['chats/' + newChatID + '/user2DisplayName'] = requests[j].displayName;

              // add users to newly created chat
              updates['userChats/' + requests[i].userID + '/' + newChatID] = true;
              updates['userChats/' + requests[j].userID + '/' + newChatID] = true;

              // notify users of the new chat
              updates['newUserChats/' + requests[i].userID] = newChatID;
              updates['newUserChats/' + requests[j].userID] = newChatID;

              // ensure user's can't be paired to eachother again
              updates['users/' + requests[i].userID + '/hasChatsWith/' + requests[j].userID] = true;
              updates['users/' + requests[j].userID + '/hasChatsWith/' + requests[i].userID] = true;

              // keep track of the paired users
              pairedUserIndices.push(i);
              pairedUserIndices.push(j);

              break;
          }
        }
      }
    } else {
      console.log('PAIR USERS: no users to pair');
    }

    // publish any updates to database
    const rootNode = db.ref();
    console.log('PAIR USERS: updating database and returning promise');
    return rootNode.update(updates);
  });
});

/**
 * Helper function to detmine whether two locations are within MAX_DISTANCE distance
 */
function withinMaxDistance(user1Latitude, user1Longitude, user2Latitude, user2Longitude) {
  if (geolib.getDistance({ latitude: user1Latitude, longitude: user1Longitude },
    { latitude: user2Latitude, longitude: user2Longitude }) <= MAX_DISTANCE) {
      console.log('PAIR USERS: withinMaxDistance returned true');
      return true;
    }
    console.log('PAIR USERS: withinMaxDistance returned false');
    return false;
}

/**
 * Helper function to detmine whether two users have any common interests
 */
function commonInterests(user1Interests, user2Interests) {
  for (let interest in user1Interests) {
    if (user2Interests[interest]) {
      console.log('PAIR USERS: commonInterests returned true');
      return true;
    }
  }
  console.log('PAIR USERS: commonInterests returned false');
  return false;
}

/**
 * Helper function to detmine whether two users are in a chat already
 */
function notPreviouslyPaired(user1Chats, user2ID) {
  console.log('PAIR USERS: notPreviouslyPaired returned ' + !(user1Chats && user1Chats[user2ID]));
  return !(user1Chats && user1Chats[user2ID]);
}
