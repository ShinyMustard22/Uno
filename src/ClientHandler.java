import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cards.Card;

public class ClientHandler implements Runnable {
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
    private static GameState board = new GameState();

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    
    private String username;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }
    }

    private boolean alreadyUsedName(String name) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (name.equals(clientHandler.username)) {
                return true;
            }
        }

        return false;
    }

    private String read() {
        try {
            String message = in.readUTF();
            //System.out.println(message);
            while (message.isEmpty()) {
                message = in.readUTF();
                System.out.println(message);
            }

            return message;
        } catch (IOException ex) {
            killEverything(socket, in, out);
            return null;
        }
    }

    private void write(String data) {
        try {
            out.writeUTF(data);
            out.flush();
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }
    }

    private void broadcastMessage(String data) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != this) {
                clientHandler.write(data);
            }
        }
    }

    private void killEverything(Socket socket, DataInputStream in, DataOutputStream out) {
        board.removePlayer(username);
        clientHandlers.remove(this);
        broadcastMessage(Server.REMOVE_PLAYER + username);
        if (clientHandlers.size() != 0 && !board.gameHasStarted()) {
            clientHandlers.get(0).write(Server.SET_LEADER);
        }

        try {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void decode(String data) {
        if (data == null) {
            return;
        }
        System.out.println("Server: " + data);
        if (board.getCurrentPlayer() != null) {
            System.out.println("1001010  " +  board.getCurrentPlayer().getUsername());
        }
        if (data.contains(Server.NAME)) {
            username = data.substring(Server.NAME.length());
            while (username.contains(" ") || alreadyUsedName(username)) {
                if (username.contains(" ")) {
                    write(Server.INVALID_USERNAME + "\n" + Server.ERROR);
                }
                
                else {
                    write(Server.TAKEN_USERNAME + "\n" + Server.ERROR);
                }
                
                username = read();
                username = username.substring(Server.NAME.length());
            }

            board.addPlayer(username);
            boolean isLeader = clientHandlers.size() == 0;

            write(Server.INIT_PLAYER_LIST + board.getPlayerList() + "\n" +
                Server.AM_LEADER + isLeader);

            broadcastMessage(Server.ADD_PLAYER + username);
            clientHandlers.add(this);
        }

        else if (data.contains(Server.GAME_STARTED)) {
            board.startGame();
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.write(Server.FIRST_CARD + board.getLastPlayedCard().toString() + "\n" +
                Server.INIT_PLAYER_HAND + board.getPlayer(clientHandler.username).getCardList());
            }
        }

        else if (!board.getPlayer(username).equals(board.getCurrentPlayer())) {
            write(Server.ERROR);
            return;
        }

        else if (data.contains(Server.PLAY_CARD)) {
            String[] cardData = data.substring(Server.PLAY_CARD.length()).split(" ");
            String strCard = cardData[0];
            int index = Integer.valueOf(cardData[1]);

            Card card = Card.decode(strCard);

            if (board.play(card, index)) {
                write(Server.PLAY_CARD + strCard + "\n" +
                        Server.SOMEBODY_PLAYED_CARD + strCard);
                broadcastMessage(Server.SOMEBODY_PLAYED_CARD + strCard);

                if (strCard.contains("draw2")){
//                    System.out.println("99999  " + board.getCurrentPlayer().getUsername());
                    String listOfCards = "";
                    String currentUserName = board.getCurrentPlayer().getUsername();
                    for
                    (Card c : drawCards(2, currentUserName)){
                        listOfCards += " " + c.toString();
                    }
//                    System.out.println("777777  " + board.getCurrentPlayer().getUsername());

                    for (ClientHandler c : clientHandlers) {
                        if (c.getUsername().equals(board.getCurrentPlayer().getUsername())) {
                            c.write(Server.DRAW_CARDS + listOfCards);
                        }
                    }

//                    System.out.println("66666  " + board.getCurrentPlayer().getUsername());

                }

                else if (strCard.contains("draw4")){
                    String listOfCards = "";
                    for (Card c : drawCards(4, board.getCurrentPlayer().getUsername())){
                        listOfCards += " " + c.toString();
                    }
                    for (ClientHandler c : clientHandlers) {
                        if (c.getUsername().equals(board.getCurrentPlayer().getUsername())) {
                            c.write(Server.DRAW_CARDS + listOfCards);
                        }
                    }

                }
            }



            else {
                write(Server.ERROR);
            }
        }

        else if (data.contains(Server.ASK_TO_DRAW)) {
//            drawCards(1, username);
//            if (card != null) {
//                write(Server.DRAW_CARD + card.toString());
//            } else {
//                board.update();
//            }
            List<Card> cards = drawCards(1, username);
            System.out.println("6" + cards);
            if (cards.isEmpty()){
                write(Server.ERROR);
            }
            else{
                write(Server.DRAW_CARDS + cards.get(0));
            }

        }

//        else if (data.contains(Server.ASK_TO_DRAW_TWO)){
//            board.advanceTurn();
//            String listOfCards = "";
//            for (Card c : drawCards(2, board.getCurrentPlayer().getUsername())){
//                listOfCards += " " + c.toString();
//            }
//            write(Server.DRAW_CARDS + listOfCards);
//        }
//
//        else if (data.contains(Server.ASK_TO_DRAW_FOUR)){
//            board.advanceTurn();
//            String listOfCards = "";
//            for (Card c : drawCards(4, board.getCurrentPlayer().getUsername())){
//                listOfCards += " " + c.toString();
//            }
//            write(Server.DRAW_CARDS + listOfCards);
//        }

    }

    public String getUsername(){
        return username;
    }
    private List<Card> drawCards(int n, String username) {
        List<Card> cards = new ArrayList<Card>();
        if (n == 0){
            return cards;
        }
        else if (n == 1){
            Card c = board.draw(username);
            if (c!= null){
                cards.add(c);
            }
        }
        else{
            for (int i = 0; i < n; i ++ ) {
                Card c = board.getDeck().remove();
                if (c == null){
                    board.update();
                    c = board.getDeck().remove();
                }
                cards.add(c);
            }
        }

        return cards;
    }

    @Override
    public void run() {
        String data;

        data = read();
        decode(data);

        while (!socket.isClosed()) {
            data = read();
            decode(data);
        } 
    }
}