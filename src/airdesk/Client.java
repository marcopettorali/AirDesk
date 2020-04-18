package airdesk;

import java.net.InetAddress;

public class Client {

    private String name;
    private InetAddress address;

    public Client(String name, InetAddress address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

}
