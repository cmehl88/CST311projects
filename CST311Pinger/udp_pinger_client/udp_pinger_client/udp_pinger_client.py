import socket
import time
import random

# Carson Mehl
# 4/22/2024
# SOURCES: developer.mozilla.org, oracle, stackoverflow.com (how pinging servers works),
# GitHub Copilot on Intellij error fixing.

HOST = "10.0.0.1"  # Server's IP address
PORT = 5000  # Port number used
TIMEOUT = 1  # Timeout will be one second

# Keep these next lines as is from previous assignment
with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
    s.connect((HOST, PORT))
    s.settimeout(TIMEOUT)  # Set socket timeout

    # List to store round-trip times (RTTs) of successful pings
    rtt_list = []
    # Variable for the counter for lost packets, starting at zero
    lost_packets = 0

    for i in range(1, 11):
        # Prepare the message and put into bytes then record time for each message then send the message
        message = "Ping{}".format(i)
        byte_msg = message.encode('utf-8')
        start_time = time.time()
        s.sendall(byte_msg)

        try:
            # using a try catch like how you would in java (I've been using java all semester)
            # Try to receive a response from the server
            # Record the end time after receiving the response and put rrt into milliseconds then put into list
            data = s.recv(1024)
            end_time = time.time()
            rtt = (end_time - start_time) * 1000
            rtt_list.append(rtt)

            #  Lastly print the message
            print("Ping {}: rtt = {:.3f} ms".format(i, rtt))

        except socket.timeout:
            # Handle timeout (packet loss)
            lost_packets += 1
            print("Ping {}: Request timed out".format(i))

    # Print summary values
    print("Summary values:")

    if rtt_list:
        # Calculate the minimum then the maximum and finally average RTT
        min_rtt = min(rtt_list)
        max_rtt = max(rtt_list)
        avg_rtt = sum(rtt_list) / len(rtt_list)

        # print all three
        print("min_rtt  = {:.3f} ms".format(min_rtt))
        print("max_rtt  = {:.3f} ms".format(max_rtt))
        print("avg_rtt = {:.3f} ms".format(avg_rtt))

    else:
        # This does not happen ever but is still good practice to put in code.
        print("No successful pings to calculate statistics.")

    # Calculate and then print the packet loss rate which should be 40%
    packet_loss_rate = (lost_packets / 10) * 100
    print("Packet loss: {:.2f}%".format(packet_loss_rate))