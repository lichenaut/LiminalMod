// automatically generated by the FlatBuffers compiler, do not modify

package com.lichenaut.liminalmod.load.LiminalMod;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class ChunkData extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_23_5_26(); }
  public static ChunkData getRootAsChunkData(ByteBuffer _bb) { return getRootAsChunkData(_bb, new ChunkData()); }
  public static ChunkData getRootAsChunkData(ByteBuffer _bb, ChunkData obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public ChunkData __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String world() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer worldAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer worldInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public int x() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int z() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public LMStructureData structures(int j) { return structures(new LMStructureData(), j); }
  public LMStructureData structures(LMStructureData obj, int j) { int o = __offset(10); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int structuresLength() { int o = __offset(10); return o != 0 ? __vector_len(o) : 0; }
  public LMStructureData.Vector structuresVector() { return structuresVector(new LMStructureData.Vector()); }
  public LMStructureData.Vector structuresVector(LMStructureData.Vector obj) { int o = __offset(10); return o != 0 ? obj.__assign(__vector(o), 4, bb) : null; }

  public static int createChunkData(FlatBufferBuilder builder,
      int worldOffset,
      int x,
      int z,
      int structuresOffset) {
    builder.startTable(4);
    ChunkData.addStructures(builder, structuresOffset);
    ChunkData.addZ(builder, z);
    ChunkData.addX(builder, x);
    ChunkData.addWorld(builder, worldOffset);
    return ChunkData.endChunkData(builder);
  }

  public static void startChunkData(FlatBufferBuilder builder) { builder.startTable(4); }
  public static void addWorld(FlatBufferBuilder builder, int worldOffset) { builder.addOffset(0, worldOffset, 0); }
  public static void addX(FlatBufferBuilder builder, int x) { builder.addInt(1, x, 0); }
  public static void addZ(FlatBufferBuilder builder, int z) { builder.addInt(2, z, 0); }
  public static void addStructures(FlatBufferBuilder builder, int structuresOffset) { builder.addOffset(3, structuresOffset, 0); }
  public static int createStructuresVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startStructuresVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endChunkData(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public ChunkData get(int j) { return get(new ChunkData(), j); }
    public ChunkData get(ChunkData obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

