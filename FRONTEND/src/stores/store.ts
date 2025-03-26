import { configureStore } from "@reduxjs/toolkit";
import userReducer from "./slices/userSlice"; // Correct import for the user slice reducer
import webSocketReducer from "./slices/webSocketSlice"; // Correct import for the user slice reducer
import roomReducer from "./slices/roomSlice"; // Correct import for the user slice reducer
import gameReducer from "./slices/gameSlice"
const store = configureStore({
  reducer: {
    user: userReducer,
    room: roomReducer,
    websocket: webSocketReducer,
    game: gameReducer
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;
