const dialogflow = require("dialogflow");
const uuid = require("uuid");
const express = require("express");
const StreamChat = require("stream-chat").StreamChat;
const cors = require("cors");
const dotenv = require("dotenv");

const port = process.env.PORT || 5200;

async function runSample(text, projectId = process.env.GOOGLE_PROJECT_ID) {
  const sessionId = uuid.v4();

  const sessionClient = new dialogflow.SessionsClient();
  const sessionPath = sessionClient.sessionPath(projectId, sessionId);

  const request = {
    session: sessionPath,
    queryInput: {
      text: {
        text: text,
        languageCode: "en-US",
      },
    },
  };

  const responses = await sessionClient.detectIntent(request);

  const result = responses[0].queryResult;
  if (result.action === "input.unknown") {
    // If unknown, return the original text
    return text;
  }

  return result.fulfillmentText;
}

dotenv.config();

const app = express();
app.use(express.json());
app.use(cors());

const client = new StreamChat(process.env.API_KEY, process.env.API_SECRET);

const channel = client.channel("messaging", "dialogflow", {
  name: "Dialogflow chat",
  created_by: { id: "admin" },
});

app.post("/dialogflow", async (req, res) => {
  const { text } = req.body;

  if (text === undefined || text.length == 0) {
    res.status(400).send({
      status: false,
      message: "Text is required",
    });
    return;
  }

  runSample(text)
    .then((text) => {
      channel
        .sendMessage({
          text: text,
          user: {
            id: "admin",
            image: "https://bit.ly/2TIt8NR",
            name: "Admin bot",
          },
        })
        .then((res) => console.log(res))
        .catch((err) => console.log(err));
      res.json({
        status: true,
        text: text,
      });
    })
    .catch((err) => {
      console.log(err);
      res.json({
        status: false,
      });
    });
});

app.post("/auth", async (req, res) => {
  const username = req.body.username;

  const token = client.createToken(username);

  await client.updateUser({ id: username, name: username }, token);

  await channel.create();

  await channel.addMembers([username, "admin"]);

  await channel.sendMessage({
    text: "Welcome to this channel. Ask me few questions",
    user: { id: "admin" },
  });

  res.json({
    status: true,
    token,
    username,
  });
});

app.listen(port, () => console.log(`App listening on port ${port}!`));
