# Reading 21-Sockets & Networking

## Client/server design pattern

In this pattern there are two kinds of processes: clients and servers.

A client initiates the communication by connecting to a server. The client sends requests to the server, and the server sends replies back. Finally, the client disconnects. A server might handle connections from many clients concurrently, and clients might also connect to multiple servers.

On the Internet, client and server processes are often running on different machines, connected only by the network, but it doesn’t have to be that way — t**he server can be a process running on the same machine as the client.**

## Network sockets

### IP addresses

A network interface is identified by an IP address. Pv4 addresses are 32-bit numbers written in four 8-bit parts. For example (as of this writing):

- `127.0.0.1 `is the [loopback ](https://en.wikipedia.org/wiki/Loopback)or [localhost ](https://en.wikipedia.org/wiki/Localhost)address: it always refers to the local machine. Technically, any address whose first octet is `127 `is a loopback address, but `127.0.0.1 `is standard.

You can [ask Google for your current IP address ](https://www.google.com/search?q=my+ip). In general, as you carry around your laptop, every time you connect your machine to the network it can be assigned a new IP address.

### Hostnames

Hostnames are names that can be translated into IP addresses. A single hostname can map to different IP addresses; and multiple hostnames can map to the same IP address. For example:

- `web.mit.edu `is the name for MIT’s web server. You can translate this name to an IP address yourself using `dig `, `host `, or `nslookup `on the command line, e.g.:

  ```shell
  $ dig +short web.mit.edu
  18.9.22.69
  ```

- `localhost `is a name for `127.0.0.1 `. When you want to talk to a server running on your own machine, talk to `localhost `.

Translation from hostnames to IP addresses is the job of the [Domain Name System (DNS) ](https://en.wikipedia.org/wiki/Domain_Name_System). It’s super cool, but not part of our discussion today.

### Port numbers

A single machine might have mutiple server applications that clients wish to connect to, so we need a way to direct traffic on the same network interface to different processes.

Network interfaces have multiple [ports ](https://en.wikipedia.org/wiki/Port_(computer_networking))identified by a 16-bit number from 0 (which is reserved, so we effectively start at 1) to 65535.

A server process binds to a particular port — it is now **listening** on that port. Clients have to know which port number the server is listening on. There are some [well-known ports ](https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers#Well-known_ports)which are reserved for system-level processes and provide standard ports for certain services. For example:

- Port 22 is the standard SSH port. When you connect to `athena.dialup.mit.edu `using SSH, the software automatically uses port 22.
- Port 25 is the standard email server port.
- Port 80 is the standard web server port. When you connect to the URL `http://web.mit.edu `in your web browser, it connects to `18.9.22.69 `on port 80.

When the port is not a standard port, it is specified as part of the address. For example, the URL `http://128.2.39.10:9000 `refers to port 9000 on the machine at `128.2.39.10 `.

When a client connects to a server, that outgoing connection also uses a port number on the client’s network interface, usually chosen at random from the available *non* -well-known ports.

### Network sockets

A [**socket** ](https://en.wikipedia.org/wiki/Network_socket)represents one end of the connection between client and server.

- A **listening socket** is used by a server process to wait for connections from remote clients.

  In Java, use [`ServerSocket `](https://docs.oracle.com/javase/8/docs/api/?java/net/ServerSocket.html)to make a listening socket, and use its [`accept `](https://docs.oracle.com/javase/8/docs/api/java/net/ServerSocket.html#accept--)method to listen to it.

- A **connected socket** can send and receive messages to and from the process on the other end of the connection. It is identified by both the local IP address and port number plus the remote address and port, which allows a server to differentiate between concurrent connections from different IPs, or from the same IP on different remote ports.

  In Java, clients use a [`Socket `](https://docs.oracle.com/javase/8/docs/api/?java/net/Socket.html)constructor to establish a socket connection to a server. Servers obtain a connected socket as a `Socket `object returned from `ServerSocket.accept `

## I/O

### Buffers

The data that clients and servers exchange over the network is sent in chunks. These are rarely just byte-sized chunks, although they might be. The sending side (the client sending a request or the server sending a response) typically writes a large chunk (maybe a whole string like “HELLO, WORLD!” or maybe 20 megabytes of video data). The network chops that chunk up into packets, and each packet is routed separately over the network. At the other end, the receiver reassembles the packets together into a stream of bytes.

The result is a bursty kind of data transmission — the data may already be there when you want to read them, or you may have to wait for them to arrive and be reassembled.

When data arrive, they go into a **buffer** , an array in memory that holds the data until you read it.
