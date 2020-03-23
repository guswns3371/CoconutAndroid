package com.example.coconut

class Constant {
    companion object{
        const val BASE_URL = "http://13.124.238.15"
        const val NODE_URL = "${BASE_URL}:9000"
        const val REGISTER_PAGE = 0
        const val PASSWORD_FIND_PAGE =1
        const val HOME_PAGE=2
        const val EMAIL_VERIFY_PAGE=3
        const val LOGIN_PAGE=4
        const val CHAT_PAGE=5
        const val CALL_PAGE=6
    }
}

class IntentID{
    companion object{
        const val EMAIL = "EMAIL"
        const val NAME = "NAME"
        const val USER_ID = "USER_ID"
        const val USER_IMAGE = "USER_IMAGE"
        const val ID = "ID"
        const val PEOPLE_IDS = "PEOPLE_IDS"
        const val USER_RESPONSE ="USER_RESPONSE"

        const val CHAT_ROOM_ID = "CHAT_ROOM_ID"
        const val CHAT_ROOM_PEOPLE_LIST = "CHAT_ROOM_PEOPLE_LIST"
        const val CHAT_ROOM_PEOPLE_INFOS = "CHAT_ROOM_PEOPLE_INFOS"

        const val CHAT_ROOM_TITLE = "CHAT_ROOM_TITLE"
        const val CHAT_MODE = "CHAT_MODE"
        const val CHAT_WITH_ME = 0
        const val CHAT_WITH_ONE_PARTNER = 1
        const val CHAT_WITH_PEOPLE_FROM_CHAT_FRAG = 2
        const val CHAT_WITH_PEOPLE_FROM_INVITING= 3
        const val CHAT_FROM_NOTIFICATION= 4

        const val PROFILE_IMAGE = 0
        const val BACKGROUND_IMAGE = 1
    }
}

class SocketReceive{
    companion object{
        const val ONLINE_USER = "__online_user"

        const val OFFLINE_USER = "__offline_user"
        const val OFFLINE_USER_WHOISON = "whoIsOn"
        const val OFFLINE_USER_DISCONNECTED = "disconnected"

        const val ON_TYPING = "__on_typing"

        const val CHATROOM_ENTER = "__chat_room_enter"
        const val CHATROOM_EXIT = "__chat_room_exit"
        const val CHAT_MESSAGE = "__chat_message"
        const val CHAT_LIST_UPDATE = "__chat_message_not_in_room"
    }
}

class SocketData{
    companion object{
        const val USER_ID = "id"
        const val CHAT_ROOM_ID = "chat_room_id"
        const val CHAT_CURRENT_ROOM_PEOPLE = "chat_current_room_people"

    }
}

class SocketSend{
    companion object{
        const val ONLINE_USER = "online_user"
        const val OFFLINE_USER = "offline_user"
        const val ON_TYPING = "on_typing"

        const val CHATROOM_ID_FOR_JOIN = "chat_room_id"
        const val CHATROOM_ENTER = "chat_room_enter"
        const val CHATROOM_EXIT = "chat_room_exit"
        const val CHAT_MESSAGE = "chat_message"
    }
}