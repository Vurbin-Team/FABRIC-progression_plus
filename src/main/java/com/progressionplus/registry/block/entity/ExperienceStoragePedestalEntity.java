package com.progressionplus.registry.block.entity;

import com.progressionplus.Progressionplus;
import com.progressionplus.registry.ModItems;
import com.progressionplus.registry.block.ImplementedInventory;
import com.progressionplus.registry.block.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.Nullable;

public class ExperienceStoragePedestalEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private float rotation = 0;
    private int storedExperience = 0;

    public ExperienceStoragePedestalEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPERIENCE_PEDESTAL_BE, pos, state);
    }

    @Override
    public void clear() {
        inventory.set(0, ItemStack.EMPTY);
        markDirty();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public int getStoredExperience() {
        return storedExperience;
    }

    public void addExperience(int experience) {
        this.storedExperience += experience;
        markDirty();
    }

    public boolean hasCrystal() {
        ItemStack stack = getStack(0);
        if (stack.isEmpty()) return false;

        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        Identifier insertItemId = Registries.ITEM.getId(ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2);
        return insertItemId.equals(itemId);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable net.minecraft.util.math.Direction side) {
        if (slot != 0) return false;

        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        Identifier insertItemId = Registries.ITEM.getId(ModItems.YELLOW_SKINT_CRYSTAL_SHARD_2);
        return insertItemId.equals(itemId);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack.copyWithCount(1));
        markDirty();
    }

    public boolean tryStorePlayerExperience(PlayerEntity player) {
        if (!hasCrystal()) {
            return false;
        }

        int playerExp = player.totalExperience;
        if (playerExp > 0) {
            addExperience(playerExp);
            player.totalExperience = 0;
            player.experienceLevel = 0;
            player.experienceProgress = 0.0f;
            return true;
        }
        return false;
    }

    public boolean tryGiveExperienceToPlayer(PlayerEntity player) {
        if (storedExperience > 0) {
            player.addExperience(storedExperience);
            storedExperience = 0;
            markDirty();
            return true;
        }
        return false;
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("StoredExperience", storedExperience);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        if (nbt.contains("StoredExperience")) {
            storedExperience = nbt.getInt("StoredExperience");
        } else {
            storedExperience = 0;
        }
    }


    @Override
    public Text getDisplayName() {
        return Text.literal("Experience storage pedestal");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    @Override
    public void markDirty() {
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        super.markDirty();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        // Можно добавить дополнительные данные для синхронизации
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeInt(storedExperience);
    }
}