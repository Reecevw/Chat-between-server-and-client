public class ClientApplication {
    public static void main(String[] args) {
        ClientProcessor client = new ClientProcessor("127.0.0.1");
        client.execute();
    }
}