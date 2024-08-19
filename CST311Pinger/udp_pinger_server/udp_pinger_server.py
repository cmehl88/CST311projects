# udp_pinger_server_starter.py
# We will need the following module to generate
# randomized lost packets
import random
from socket import socket, AF_INET, SOCK_DGRAM

# Create a UDP socket
# Notice the use of SOCK_DGRAM for UDP packets
serverSocket = socket(AF_INET, SOCK_DGRAM)
# Assign IP address and port number to socket
serverSocket.bind(('', 12000))
pingnum = 0
while True:
    # Count the pings received
    pingnum += 1
    # Generate random number in the range of 1 to 10
    rand = random.randint(1, 10)
    # Receive the client packet along with the
    # address it is coming from
    message, address = serverSocket.recvfrom(1024)
    # If rand is less is than 4, and this not the
    # first "ping" of a group of 10, consider the
    # packet lost and do not respond
    if rand < 4 and pingnum % 10 != 1:
        continue
    # Otherwise, the server responds
    serverSocket.sendto(message, address)

# The server sits in an infinite loop listening for incoming UDP packets. 
# When a packet comes in and if a randomized integer is less than 4, 
# the server does not send a reply message back to the client.
