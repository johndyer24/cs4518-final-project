const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const db = admin.database();

const _ = require('lodash');

exports.pairUsers = functions.database.ref('chatRequests').onWrite((event) => {

  db.ref('pairUsersFunctionRan').once('value').then((snapshot) => {
    if (snapshot.exists()) {
      return db.ref('pairUsersFunctionRan').set(null);
    } else {
      let requests = event.data.val(); // get object containing list of request objects
      for (let key in requests) {
        requests[key].requestID = key;
      }
      requests = _.orderBy(requests, 'time', 'asc'); // Use Lodash to create array of requests sorted by value of 'time'
      let updates = {};
      for (let i = 0; i < requests.length - 1; i += 2) {
        updates['users/' + requests[i].userID + '/requestedChat'] = null;
        updates['users/' + requests[i+1].userID + '/requestedChat'] = null;
        updates['chatRequests/' + requests[i].requestID] = null;
        updates['chatRequests/' + requests[i+1].requestID] = null;
        let newChatID = db.ref('chats').push().key;
        updates['chats/' + newChatID + '/startTime'] = admin.database.ServerValue.TIMESTAMP;
        updates['userChats/' + requests[i].userID + '/' + newChatID] = true;
        updates['userChats/' + requests[i+1].userID + '/' + newChatID] = true;
      }

      const rootNode = event.data.ref.parent;
      return rootNode.update(updates);
    }
  });
});
