package by.dero.gvh.lobby;

import by.dero.gvh.model.storages.LocalStorage;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;

import java.io.FileInputStream;

public class PlayerLobby {
    private LobbyRecord record;

    public PlayerLobby(LobbyRecord record) {
        this.record = record;
    }

    public void create() {
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(Lobby.getInstance().getLobbySchematicFile());
            ClipboardReader reader = format.getReader(new FileInputStream(Lobby.getInstance().getLobbySchematicFile()));
            Clipboard clipboard = reader.read();
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(
                    (World) Lobby.getInstance().getWorld(), -1);
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(record.getPosition().getX(), record.getPosition().getY(), record.getPosition().getZ()))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void destroy() {

    }

    public LobbyRecord getRecord() {
        return record;
    }
}
