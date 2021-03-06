package com.superzanti.serversync.client;

import com.superzanti.serversync.files.FileManifest;
import com.superzanti.serversync.util.Logger;
import com.superzanti.serversync.util.PrettyCollection;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * The sync process for clients.
 *
 * @author Rheimus
 */
public class ClientWorker implements Runnable, Closeable {
    private String address;
    private int port = -1;

    private Server server;
    private Mode2Sync sync;

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Generate a list of actions required to synchronize with the server.
     * <p>
     * This method requires an address and port to be configured via setAddress & setPort.
     *
     * @return A list of actions
     */
    public Callable<List<ActionEntry>> fetchActions() {
        return () -> {
            FileManifest manifest = sync.fetchManifest();
            Logger.log("Determining actions for manifest");
            Logger.log(PrettyCollection.get(manifest.directories));
            Logger.log(PrettyCollection.get(manifest.files));
            return sync.generateActionList(manifest);
        };
    }

    public Callable<Void> executeActions(List<ActionEntry> actions, Consumer<ActionProgress> progressConsumer) {
        return () -> {
            Logger.log(String.format("Executing actions: %s", PrettyCollection.get(actions)));
            sync.executeActionList(actions, progressConsumer);
            return null;
        };
    }

    public void connect() throws Exception {
        if (address == null || port == -1) {
            // TODO use a real exception here
            throw new Exception("Attempted to call connect without an address or port configured");
        }
        Client client = new Client(address, port);
        this.server = Server.forClient(client);
        if (!server.connect()) {
            // TODO use a real exception here
            throw new Exception("Failed to connect to server");
        }

        this.sync = Mode2Sync.forServer(server);
    }

    public void close() {
        if (this.server != null) {
            this.server.close();
            this.server = null;
        }
        this.sync = null;
    }

    @Override
    public void run() {
//        updateHappened = false;

//        Logger.getLog().clearUserFacingLog();
//
//        Client client = new Client(config.SERVER_IP, config.SERVER_PORT);
//        server = Server.forClient(client);
//
//        if (!server.connect()) {
//            errorInUpdates = true;
//            closeWorker();
//            return;
//        }
//
//        ServerInfo serverInfo = server.info;
//
//        Mode2Sync sync = Mode2Sync.forServer(server);
//        try {
//            List<ActionEntry> actions = sync.generateActionList(sync.fetchManifest());
//            sync.executeActionList(actions);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//        updateHappened = true;
//        closeWorker();
//
//        // Update configured server to the latest used address
//        // consideration to be had here for client silent sync mode
//        //TODO fix or delete this
//        if (ServerSync.clientGUI != null) {
//            config.updateServerDetails(ServerSync.clientGUI.getIPAddress(), ServerSync.clientGUI.getPort());
//        }
//
//        Logger.log(ServerSync.strings.getString("update_complete"));
    }

//    private void enableGuiButton() {
//        Gui_JavaFX.getStackMainPane().getPaneSync().getBtnSync().setDisable(false);
//        Gui_JavaFX.getStackMainPane().getPaneSync().getBtnCheckUpdate().setDisable(false);
//    }
}
