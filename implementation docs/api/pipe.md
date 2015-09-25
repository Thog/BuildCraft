Pipe API
========

Core API
--------

The core API is composed of the pipe registry, definitions and behaviours.

The registry contains methods for registering pipe definitions and getting a definition, pipe item or global name from any of the other three.

A pipe definition is a single instance per pipe type, like an item or block. Unlike a block or item however it is final, and can only contain the necessary information to create a pipe (The textures, unique tag, the type of pipe and a behaviour factory).

A pipe behaviour object is created once for each pipe block placed in the world- it handles the state information, reacts to events and transfers the state information between NBT and itself.

Events
------

Pipe behaviour objects can receive events by annotating a single argument method with the @Subscribe annotation (from com.google.common.eventbus). The argument must be a subinterface of IPipeEvent

````

@Subscribe
public void onEvent(IPipeEvent event) {
	
}
````

The hierachy for events is as follows:

IPipeEvent
 |  The main event, this can be quered for the IPipe object that the event is fired on.
 |	
 +--IPipeEventTick
 |      Fired every tick from the pipe object.
 |		
 +--IPipeEventRandomDisplayTick
 |      Fired whenever the pipe block has the randomDisplayTick method called
 |
 +--IPipeEventConnection
 | |    Fired whenever a connection changes with a pipe. The most useful events to listen to are its subtypes.
 | |
 | +--IPipeEventConnect
 | | |    Fired whenever a tile entity is placed down next to the pipe that could be connected to. It is important to note that it may just be a normal block (for example, dirt) that cannot currently connect. If that is the case, calling isCorrectType() will return if the pipe should normally be able to connect.
 | | |
 | | +--IPipeEventConnectBlock
 | | |      Fired specifically whenever a block attempts to connect to this pipe.
 | | |
 | | +--IPipeEventConnectPipe
 | |        Fired specifically whenever a pipe attempts to connect to another pipe. You can call getConnectingPipe() to get the other pipe
 | |
 | +--IPipeEventDisconnect
 |        Fired whenever a connection is removed from a pipe. Note that you cannot cancel this like a pipe connection event.
 |
 +--IPipeEvent