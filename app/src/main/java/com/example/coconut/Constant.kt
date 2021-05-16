package com.example.coconut

class Constant {
    companion object{
        // 127.0.0.1은 안드로이드 VM 자체의 주소를 의미한다.
        // pc로컬 주소로 연결하기 위해선 10.0.2.2 를 사용할 것
        // public 주소를 사용하면 외부에서 연결 가능하다
        private const val BASE_HOST = "10.0.2.2"
        const val BASE_URL = "http://${BASE_HOST}"
        const val SPRING_BOOT_URL = "${BASE_URL}:8080/"
        const val SPRING_BOOT_IMAGE_URL = "${BASE_URL}:8080/uploads/"
        const val SOCKET_SERVER = SPRING_BOOT_URL
        const val STOMP_URL = "ws://${BASE_HOST}:8080/socket-endpoint/websocket"
        const val REGISTER_PAGE = 0
        const val PASSWORD_FIND_PAGE =1
        const val HOME_PAGE=2
        const val EMAIL_VERIFY_PAGE=3
        const val LOGIN_PAGE=4
        const val CHAT_PAGE=5
        const val CALL_PAGE=6
        const val RC_FAIL = 0
        const val RC_AUTH = 100
    }
}
class BroadCastIntentID {
    companion object{
        const val SEND_USER_LIST = "SEND_USER_LIST"
        const val SEND_LOGOUT = "SEND_LOGOUT"
        const val SEND_ON_CONNECT = "SEND_ON_CONNECT"
        const val SEND_ON_DISCONNECT = "SEND_ON_DISCONNECT"
        const val SEND_ON_ERROR = "SEND_ON_ERROR"
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
        const val CHAT_IMAGE = 2
        const val CHAT_IMAGES = "CHAT_IMAGES"
        const val CHAT_IMAGE_INDEX = "CHAT_IMAGE_INDEX"

        const val RECEIVE_USER_LIST = "RECEIVE_USER_LIST"
        const val RECEIVE_LOGOUT = "RECEIVE_LOGOUT"
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

class MessageType{
    companion object{
        const val TEXT = "TEXT"
        const val IMAGE = "IMAGE"
        const val FILE = "FILE"
        const val INFO = "INFO"
    }
}

class RoomType{
    companion object{
        const val GROUP = "GROUP"
        const val ME = "ME"
    }
}