# lamport-algo-enhanced-java
Implementation of an enhanced version of Lamport's Distributed Mutual Exclusion Algorithm in Java, designed to detect and handle process failures. 

The application consists of ten processes sharing a critical resource, communicating via sockets, and utilizing threading for parallel processing. An eleventh process is dedicated to failure detection and notifying active processes of failed ones. The project includes a graphical interface for real-time visualization of process states, timestamps (estampilles), and queue statuses.
