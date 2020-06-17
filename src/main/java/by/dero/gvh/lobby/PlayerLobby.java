package by.dero.gvh.lobby;

import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.utils.WorldEditUtils;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;

public class PlayerLobby {
    private LobbyRecord record;

    public PlayerLobby(LobbyRecord record) {
        this.record = record;
    }

    public void create() {
        WorldEditUtils.pasteSchematic(Lobby.getInstance().getLobbySchematicFile(), Lobby.getInstance().getWorld(),
                record.getPosition());
    }

    public void destroy() {

    }

    public LobbyRecord getRecord() {
        return record;
    }
}
