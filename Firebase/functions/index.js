/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const { onRequest } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const admin = require('firebase-admin');

admin.initializeApp();

// This registration token comes from the client FCM SDKs.
const registrationToken = "dcqddV13SrijbSqc87WNqt:APA91bGhSkjl5mp6zJueLuqYMCjQ3-CKXIIh8XzU74ubumPMiLsRjryPorGN3pwnYTKYqT90vPwQybk8R3-Zajuev3TywIfhoUAZlV6iWloL__-xzUPzDvpJfle-nvkwsPVvTAqDBGvN";

const message = {
  data: {
    score: "850",
    time: "2:45",
  },
  notification: {
    title: "Mama says...",
    body: "Time's up!",
  },
  token: registrationToken,
};

// Send a message to the device corresponding to the provided registration token.
exports.definitely = onRequest(async (req, res) => {
  admin.messaging().send(message);
  res.json({ result: admin.app.name });
});
