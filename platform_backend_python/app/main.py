from fastapi import FastAPI, APIRouter
from jwt_auth.routers import jwt_auth_router
from totp_auth.routers import totp_auth_router
from users.routers import users_router

app = FastAPI()

base_router = APIRouter(prefix="/api/v1")

base_router.include_router(jwt_auth_router)
base_router.include_router(users_router)
base_router.include_router(totp_auth_router)

app.include_router(base_router)


@app.get("/")
async def root():
    return {"message": "Hello World"}