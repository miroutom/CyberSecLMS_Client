from fastapi import HTTPException
from starlette import status

unauthorized_exception = HTTPException(
    detail=f'Invalid username or password or security code',
    status_code=status.HTTP_401_UNAUTHORIZED,
)