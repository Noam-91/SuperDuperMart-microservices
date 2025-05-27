import { Eureka } from 'eureka-js-client';
import { WebSocketServer } from 'ws'; 
import http from 'http';
import express from 'express';
import cors from 'cors';
import os from 'os';

const WS_PORT = 8084;
const APP_PORT = 8085; 
const FRONTEND_URL = 'http://localhost:4200';

// Create express app for health checks
const app = express();
app.use(cors({
  origin: [FRONTEND_URL], 
  credentials: true
}));
app.get('/health', (req, res) => res.status(200).json({ status: 'UP' }));
app.get('/info', (req, res) => res.status(200).json({ 
  service: 'WebSocket Assistant',
  status: 'Running',
  port: WS_PORT
}));

const healthServer = http.createServer(app);
healthServer.listen(APP_PORT, () => {
  console.log(`Health check server running on port ${APP_PORT}`);
});

// WebSocket Server
const wss = new WebSocketServer({ port: WS_PORT }, () => {
  console.log(`WebSocket server listening on port ${WS_PORT}`);
});

// Connection handling
wss.on('connection', (ws) => {
  const clientIp = ws._socket.remoteAddress;
  console.log(`Client connected from ${clientIp}`);

  // Send welcome message
  sendMessage(ws, 'Hello! I am your assistant. How can I help you today?');

  // Message handling
  ws.on('message', (message) => handleClientMessage(ws, message));

  // Cleanup on close
  ws.on('close', () => {
    console.log(`Client ${clientIp} disconnected`);
  });

  // Error handling
  ws.on('error', (error) => {
    console.error(`WebSocket error with ${clientIp}:`, error);
  });
});


function sendMessage(ws, payload) {
  try {
    ws.send(JSON.stringify({
      type: 'assistant',
      payload: payload
    }));
  } catch (error) {
    console.error('Failed to send message:', error);
  }
}

function handleClientMessage(ws, message) {
  try {
    const messageString = message.toString();
    console.log(`Received: ${messageString}`);
    
    const parsedMessage = JSON.parse(messageString);
    
    if (parsedMessage.type === 'user' && parsedMessage.payload) {
      processUserQuery(ws, parsedMessage.payload);
    } else {
      console.warn('Unexpected message format:', parsedMessage);
      sendMessage(ws, 'Sorry, I did not understand that format.');
    }
  } catch (error) {
    console.error('Message processing error:', error);
    sendMessage(ws, 'Error processing your request.');
  }
}

function processUserQuery(ws, query) {
  const lowerQuery = query.toLowerCase();
  let response = '';
  
  const responseMap = {
    'hello': 'Hello there! How can I assist you?',
    'hi': 'Hello there! How can I assist you?',
    'time': `The current time is ${new Date().toLocaleTimeString()}.`,
    'date': `Today's date is ${new Date().toLocaleDateString()}.`,
    'product': 'Are you looking for information about a specific product?',
    'design': "Are you looking for this book: \nSystem Design Interview – An Insider's Guide? http://localhost:4200/product/1",
    'thank': "You're welcome! Is there anything else?"
  };

  for (const [keyword, reply] of Object.entries(responseMap)) {
    if (lowerQuery.includes(keyword)) {
      response = reply;
      break;
    }
  }

  response = response || 'I am not sure how to respond to that. Can you rephrase?';
  sendMessage(ws, response);
}

// Eureka Client with error handling
const client = new Eureka({
  instance: {
    app: 'assistant-service',
    instanceId: `${os.hostname()}:assistant-service:${WS_PORT}`,
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    port: {
      '$': WS_PORT,
      '@enabled': true,
    },
    vipAddress: 'assistant-service',
    statusPageUrl: `http://localhost:${WS_PORT}/info`,
    healthCheckUrl: `http://localhost:${WS_PORT}/health`,
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
    registerWithEureka: true,
    fetchRegistry: true,
    leaseInfo: {
      renewalIntervalInSecs: 10,
      durationInSecs: 30
    }
  },
  eureka: {
    host: 'localhost',
    port: 8761,
    servicePath: '/eureka/apps/',
    maxRetries: 10,
    requestRetryDelay: 2000
  }
});

client.start(error => {
  if (error) {
    console.error('Eureka registration failed:', error);
  } else {
    console.log('Successfully registered with Eureka');
  }
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('Shutting down gracefully...');
  client.stop();
  wss.close();
  healthServer.close();
  process.exit();
});