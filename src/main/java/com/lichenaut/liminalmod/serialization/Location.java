package com.lichenaut.liminalmod.serialization;// automatically generated by the FlatBuffers compiler, do not modify

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class Location extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_23_5_26(); }
  public static Location getRootAsLocation(ByteBuffer _bb) { return getRootAsLocation(_bb, new Location()); }
  public static Location getRootAsLocation(ByteBuffer _bb, Location obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public Location __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String world() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer worldAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer worldInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public double x() { int o = __offset(6); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }
  public double y() { int o = __offset(8); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }
  public double z() { int o = __offset(10); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }

  public static int createLocation(FlatBufferBuilder builder,
      int worldOffset,
      double x,
      double y,
      double z) {
    builder.startTable(4);
    Location.addZ(builder, z);
    Location.addY(builder, y);
    Location.addX(builder, x);
    Location.addWorld(builder, worldOffset);
    return Location.endLocation(builder);
  }

  public static void startLocation(FlatBufferBuilder builder) { builder.startTable(4); }
  public static void addWorld(FlatBufferBuilder builder, int worldOffset) { builder.addOffset(0, worldOffset, 0); }
  public static void addX(FlatBufferBuilder builder, double x) { builder.addDouble(1, x, 0.0); }
  public static void addY(FlatBufferBuilder builder, double y) { builder.addDouble(2, y, 0.0); }
  public static void addZ(FlatBufferBuilder builder, double z) { builder.addDouble(3, z, 0.0); }
  public static int endLocation(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public Location get(int j) { return get(new Location(), j); }
    public Location get(Location obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

