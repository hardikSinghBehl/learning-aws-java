package com.upwork.chatbot;

import java.util.UUID;

record ChatResponse(UUID chatId, String answer) {}