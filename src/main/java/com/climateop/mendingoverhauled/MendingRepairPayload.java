package com.climateop.mendingoverhauled;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record MendingRepairPayload(int slotIndex) implements CustomPacketPayload {

    public static final Identifier MENDING_REPAIR_ID =
            MendingOverhauled.id("mending_repair");

    public static final CustomPacketPayload.Type<MendingRepairPayload> TYPE =
            new CustomPacketPayload.Type<>(MENDING_REPAIR_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, MendingRepairPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    MendingRepairPayload::slotIndex,
                    MendingRepairPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}