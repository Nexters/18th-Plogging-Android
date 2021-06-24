package com.plogging.ecorun.event

sealed class EventImpl {
    class LogoutEvent : Event
    class NetworkErrorEvent: Event
    class ServerErrorEvent: Event
}