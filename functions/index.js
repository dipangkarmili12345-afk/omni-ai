const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * Provider configuration mappings and model definitions
 */
const PROVIDER_CONFIG = {
  gemini: {
    name: "Gemini",
    envKey: "GEMINI_API_KEY",
    defaultModel: "gemini-2.5-flash",
  },
  chatgpt: {
    name: "ChatGPT",
    envKey: "OPENAI_API_KEY",
    defaultModel: "gpt-4o",
  },
  claude: {
    name: "Claude",
    envKey: "ANTHROPIC_API_KEY",
    defaultModel: "claude-3-5-sonnet-20241022",
  },
  deepseek: {
    name: "DeepSeek",
    envKey: "DEEPSEEK_API_KEY",
    defaultModel: "deepseek-chat",
  },
  grok: {
    name: "Grok",
    envKey: "XAI_API_KEY",
    defaultModel: "grok-2-latest",
  },
};

/**
 * Normalize model identifier to supported provider
 */
function resolveProvider(modelInput) {
  if (!modelInput) return "gemini";
  const id = String(modelInput).toLowerCase().trim();
  if (id.includes("gemini")) return "gemini";
  if (id.includes("chatgpt") || id.includes("gpt") || id.includes("openai")) return "chatgpt";
  if (id.includes("claude") || id.includes("anthropic")) return "claude";
  if (id.includes("deepseek")) return "deepseek";
  if (id.includes("grok") || id.includes("xai")) return "grok";
  return "gemini";
}

/**
 * Call Gemini API via Google Generative Language REST endpoint
 */
async function callGemini(apiKey, modelName, prompt, history) {
  const url = `https://generativelanguage.googleapis.com/v1beta/models/${modelName}:generateContent?key=${apiKey}`;
  const contents = [];

  if (Array.isArray(history)) {
    for (const msg of history.slice(-6)) {
      contents.push({
        role: msg.role === "user" ? "user" : "model",
        parts: [{ text: String(msg.content || "") }],
      });
    }
  }
  contents.push({
    role: "user",
    parts: [{ text: String(prompt) }],
  });

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 45000);

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ contents }),
      signal: controller.signal,
    });
    clearTimeout(timeoutId);

    if (!res.ok) {
      const errText = await res.text();
      throw new Error(`Gemini API error (status ${res.status}): ${errText}`);
    }

    const data = await res.json();
    const candidate = data.candidates?.[0];
    if (!candidate) throw new Error("Gemini returned empty candidate list.");
    const parts = candidate.content?.parts || [];
    return parts.map((p) => p.text).join("");
  } catch (err) {
    clearTimeout(timeoutId);
    throw err;
  }
}

/**
 * Call OpenAI API for ChatGPT
 */
async function callChatGPT(apiKey, modelName, prompt, history) {
  const url = "https://api.openai.com/v1/chat/completions";
  const messages = [];

  if (Array.isArray(history)) {
    for (const msg of history.slice(-6)) {
      messages.push({
        role: msg.role === "user" ? "user" : "assistant",
        content: String(msg.content || ""),
      });
    }
  }
  messages.push({ role: "user", content: String(prompt) });

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 45000);

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${apiKey}`,
      },
      body: JSON.stringify({
        model: modelName,
        messages,
        temperature: 0.7,
      }),
      signal: controller.signal,
    });
    clearTimeout(timeoutId);

    if (!res.ok) {
      const errText = await res.text();
      throw new Error(`OpenAI API error (status ${res.status}): ${errText}`);
    }

    const data = await res.json();
    return data.choices?.[0]?.message?.content || "";
  } catch (err) {
    clearTimeout(timeoutId);
    throw err;
  }
}

/**
 * Call Anthropic API for Claude
 */
async function callClaude(apiKey, modelName, prompt, history) {
  const url = "https://api.anthropic.com/v1/messages";
  const messages = [];

  if (Array.isArray(history)) {
    for (const msg of history.slice(-6)) {
      messages.push({
        role: msg.role === "user" ? "user" : "assistant",
        content: String(msg.content || ""),
      });
    }
  }
  messages.push({ role: "user", content: String(prompt) });

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 45000);

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "x-api-key": apiKey,
        "anthropic-version": "2023-06-01",
      },
      body: JSON.stringify({
        model: modelName,
        messages,
        max_tokens: 4096,
      }),
      signal: controller.signal,
    });
    clearTimeout(timeoutId);

    if (!res.ok) {
      const errText = await res.text();
      throw new Error(`Anthropic API error (status ${res.status}): ${errText}`);
    }

    const data = await res.json();
    const blocks = data.content || [];
    return blocks
      .filter((b) => b.type === "text")
      .map((b) => b.text)
      .join("");
  } catch (err) {
    clearTimeout(timeoutId);
    throw err;
  }
}

/**
 * Call DeepSeek API
 */
async function callDeepSeek(apiKey, modelName, prompt, history) {
  const url = "https://api.deepseek.com/chat/completions";
  const messages = [];

  if (Array.isArray(history)) {
    for (const msg of history.slice(-6)) {
      messages.push({
        role: msg.role === "user" ? "user" : "assistant",
        content: String(msg.content || ""),
      });
    }
  }
  messages.push({ role: "user", content: String(prompt) });

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 45000);

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${apiKey}`,
      },
      body: JSON.stringify({
        model: modelName,
        messages,
        temperature: 0.6,
      }),
      signal: controller.signal,
    });
    clearTimeout(timeoutId);

    if (!res.ok) {
      const errText = await res.text();
      throw new Error(`DeepSeek API error (status ${res.status}): ${errText}`);
    }

    const data = await res.json();
    const choice = data.choices?.[0]?.message;
    if (!choice) return "";
    // DeepSeek Reasoner includes reasoning_content
    if (choice.reasoning_content) {
      return `[DeepSeek Reasoner]\n${choice.reasoning_content}\n\n${choice.content}`;
    }
    return choice.content || "";
  } catch (err) {
    clearTimeout(timeoutId);
    throw err;
  }
}

/**
 * Call xAI API for Grok
 */
async function callGrok(apiKey, modelName, prompt, history) {
  const url = "https://api.x.ai/v1/chat/completions";
  const messages = [];

  if (Array.isArray(history)) {
    for (const msg of history.slice(-6)) {
      messages.push({
        role: msg.role === "user" ? "user" : "assistant",
        content: String(msg.content || ""),
      });
    }
  }
  messages.push({ role: "user", content: String(prompt) });

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 45000);

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${apiKey}`,
      },
      body: JSON.stringify({
        model: modelName,
        messages,
        temperature: 0.7,
      }),
      signal: controller.signal,
    });
    clearTimeout(timeoutId);

    if (!res.ok) {
      const errText = await res.text();
      throw new Error(`xAI API error (status ${res.status}): ${errText}`);
    }

    const data = await res.json();
    return data.choices?.[0]?.message?.content || "";
  } catch (err) {
    clearTimeout(timeoutId);
    throw err;
  }
}

/**
 * Core AI dispatcher dispatching to the appropriate provider
 */
async function dispatchAiRequest(providerKey, prompt, history, customModel) {
  const provider = resolveProvider(providerKey);
  const cfg = PROVIDER_CONFIG[provider];
  const apiKey = process.env[cfg.envKey];

  if (!apiKey || apiKey.trim() === "") {
    return {
      status: "CREDENTIAL_REQUIRED",
      provider: cfg.name,
      envKey: cfg.envKey,
      message: `PROVIDER: NOT LIVE — SERVER CREDENTIAL REQUIRED (${cfg.envKey} is not configured in server environment).`,
    };
  }

  const modelName = customModel || cfg.defaultModel;
  let text = "";

  switch (provider) {
    case "gemini":
      text = await callGemini(apiKey, modelName, prompt, history);
      break;
    case "chatgpt":
      text = await callChatGPT(apiKey, modelName, prompt, history);
      break;
    case "claude":
      text = await callClaude(apiKey, modelName, prompt, history);
      break;
    case "deepseek":
      text = await callDeepSeek(apiKey, modelName, prompt, history);
      break;
    case "grok":
      text = await callGrok(apiKey, modelName, prompt, history);
      break;
    default:
      throw new Error(`Unsupported AI provider: ${provider}`);
  }

  return {
    status: "SUCCESS",
    provider: cfg.name,
    model: modelName,
    response: text,
  };
}

/**
 * HTTPS REST Endpoint for Omni AI Android App
 * URL: https://<region>-<project-id>.cloudfunctions.net/omniAiChat
 */
exports.omniAiChat = functions.https.onRequest(async (req, res) => {
  // Enable CORS
  res.set("Access-Control-Allow-Origin", "*");
  res.set("Access-Control-Allow-Methods", "POST, OPTIONS");
  res.set("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    res.status(204).send("");
    return;
  }

  if (req.method !== "POST") {
    res.status(405).json({ error: "Method Not Allowed. Use POST." });
    return;
  }

  const { model, prompt, history, customModel } = req.body || {};

  if (!prompt || typeof prompt !== "string" || prompt.trim() === "") {
    res.status(400).json({ error: "Missing or empty 'prompt' field." });
    return;
  }

  try {
    const result = await dispatchAiRequest(model, prompt.trim(), history, customModel);
    if (result.status === "CREDENTIAL_REQUIRED") {
      res.status(503).json(result);
    } else {
      res.status(200).json(result);
    }
  } catch (error) {
    console.error("Omni AI Backend Error:", error);
    res.status(500).json({
      status: "ERROR",
      error: error.message || "Internal server error executing AI request",
    });
  }
});
