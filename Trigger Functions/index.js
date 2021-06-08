const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

exports.databaseTrigger = functions.storage.object().onFinalize((object) => {
  const fileName = object.name;
  const link = object.mediaLink;
  const postKey = admin.database().ref("Images/").push().key;
  const updates = {};

  const objectData = {
    fileName: fileName,
    URL: link,
  };
  updates["/Images/"+postKey] = objectData;
  admin.database().ref().update(updates);
});
