from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

app = FastAPI(title="AI Service (stub)")

class TextIn(BaseModel):
    text: str

class VectorOut(BaseModel):
    vector: List[float]

@app.get("/health")
async def health():
    return {"status": "ok"}

@app.post("/embed", response_model=VectorOut)
async def embed(payload: TextIn):
    # dummy deterministic vector for testing
    vec = [float(len(payload.text)), 0.0, 1.0]
    return {"vector": vec}

@app.post("/query")
async def query(payload: TextIn):
    # dummy response
    return {"answer": f"Echo: {payload.text}", "sources": []}
