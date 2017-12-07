// import required modules
const functions = require('firebase-functions');
const admin = require('firebase-admin'); // The Firebase Admin SDK to access the Firebase Realtime Database.
const _ = require('lodash');

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

      // iterate over requests and pair users
      for (let i = 0; i < requests.length - 1; i += 2) {
        // delete chat requests
        updates['users/' + requests[i].userID + '/requestedChat'] = null;
        updates['users/' + requests[i+1].userID + '/requestedChat'] = null;
        updates['chatRequests/' + requests[i].requestID] = null;
        updates['chatRequests/' + requests[i+1].requestID] = null;

        // create new chat
        let newChatID = db.ref('chats').push().key;
        updates['chats/' + newChatID + '/startTime'] = admin.database.ServerValue.TIMESTAMP;

        // add users to newly created chat
        updates['userChats/' + requests[i].userID + '/' + newChatID] = true;
        updates['userChats/' + requests[i+1].userID + '/' + newChatID] = true;
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
