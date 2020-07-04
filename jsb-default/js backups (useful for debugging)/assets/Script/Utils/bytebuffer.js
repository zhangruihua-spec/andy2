(function() {
(function(e, t) {
"function" == typeof define && define.amd ? define([ "assets/Script/Utils/long" ], t) : "function" == typeof require && "object" == typeof module && module && module.exports ? module.exports = function() {
var e;
try {
e = require("assets/Script/Utils/long");
} catch (e) {}
return t(e);
}() : (e.dcodeIO = e.dcodeIO || {}).ByteBuffer = t(e.dcodeIO.Long);
})(this, function(e) {
"use strict";
var t = function(e, r, f) {
"undefined" == typeof e && (e = t.DEFAULT_CAPACITY);
"undefined" == typeof r && (r = t.DEFAULT_ENDIAN);
"undefined" == typeof f && (f = t.DEFAULT_NOASSERT);
if (!f) {
if ((e |= 0) < 0) throw RangeError("Illegal capacity");
r = !!r;
f = !!f;
}
this.buffer = 0 === e ? i : new ArrayBuffer(e);
this.view = 0 === e ? null : new Uint8Array(this.buffer);
this.offset = 0;
this.markedOffset = -1;
this.limit = e;
this.littleEndian = r;
this.noAssert = f;
};
t.VERSION = "5.0.1";
t.LITTLE_ENDIAN = !0;
t.BIG_ENDIAN = !1;
t.DEFAULT_CAPACITY = 16;
t.DEFAULT_ENDIAN = t.BIG_ENDIAN;
t.DEFAULT_NOASSERT = !1;
t.Long = e || null;
var r = t.prototype;
r.__isByteBuffer__;
Object.defineProperty(r, "__isByteBuffer__", {
value: !0,
enumerable: !1,
configurable: !1
});
var i = new ArrayBuffer(0), f = String.fromCharCode;
function n(e) {
var t = 0;
return function() {
return t < e.length ? e.charCodeAt(t++) : null;
};
}
function s() {
var e = [], t = [];
return function() {
if (0 === arguments.length) return t.join("") + f.apply(String, e);
e.length + arguments.length > 1024 && (t.push(f.apply(String, e)), e.length = 0);
Array.prototype.push.apply(e, arguments);
};
}
t.accessor = function() {
return Uint8Array;
};
t.allocate = function(e, r, i) {
return new t(e, r, i);
};
t.concat = function(e, r, i, f) {
if ("boolean" == typeof r || "string" != typeof r) {
f = i;
i = r;
r = void 0;
}
for (var n, s = 0, o = 0, h = e.length; o < h; ++o) {
t.isByteBuffer(e[o]) || (e[o] = t.wrap(e[o], r));
(n = e[o].limit - e[o].offset) > 0 && (s += n);
}
if (0 === s) return new t(0, i, f);
var a, l = new t(s, i, f);
o = 0;
for (;o < h; ) if (!((n = (a = e[o++]).limit - a.offset) <= 0)) {
l.view.set(a.view.subarray(a.offset, a.limit), l.offset);
l.offset += n;
}
l.limit = l.offset;
l.offset = 0;
return l;
};
t.isByteBuffer = function(e) {
return !0 === (e && e.__isByteBuffer__);
};
t.type = function() {
return ArrayBuffer;
};
t.wrap = function(e, i, f, n) {
if ("string" != typeof i) {
n = f;
f = i;
i = void 0;
}
if ("string" == typeof e) {
"undefined" == typeof i && (i = "utf8");
switch (i) {
case "base64":
return t.fromBase64(e, f);

case "hex":
return t.fromHex(e, f);

case "binary":
return t.fromBinary(e, f);

case "utf8":
return t.fromUTF8(e, f);

case "debug":
return t.fromDebug(e, f);

default:
throw Error("Unsupported encoding: " + i);
}
}
if (null === e || "object" != typeof e) throw TypeError("Illegal buffer");
var s;
if (t.isByteBuffer(e)) {
(s = r.clone.call(e)).markedOffset = -1;
return s;
}
if (e instanceof Uint8Array) {
s = new t(0, f, n);
if (e.length > 0) {
s.buffer = e.buffer;
s.offset = e.byteOffset;
s.limit = e.byteOffset + e.byteLength;
s.view = new Uint8Array(e.buffer);
}
} else if (e instanceof ArrayBuffer) {
s = new t(0, f, n);
if (e.byteLength > 0) {
s.buffer = e;
s.offset = 0;
s.limit = e.byteLength;
s.view = e.byteLength > 0 ? new Uint8Array(e) : null;
}
} else {
if ("[object Array]" !== Object.prototype.toString.call(e)) throw TypeError("Illegal buffer");
(s = new t(e.length, f, n)).limit = e.length;
for (var o = 0; o < e.length; ++o) s.view[o] = e[o];
}
return s;
};
r.writeBitSet = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if (!(e instanceof Array)) throw TypeError("Illegal BitSet: Not an array");
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
var i, f = t, n = e.length, s = n >> 3, o = 0;
t += this.writeVarint32(n, t);
for (;s--; ) {
i = 1 & !!e[o++] | (1 & !!e[o++]) << 1 | (1 & !!e[o++]) << 2 | (1 & !!e[o++]) << 3 | (1 & !!e[o++]) << 4 | (1 & !!e[o++]) << 5 | (1 & !!e[o++]) << 6 | (1 & !!e[o++]) << 7;
this.writeByte(i, t++);
}
if (o < n) {
var h = 0;
i = 0;
for (;o < n; ) i |= (1 & !!e[o++]) << h++;
this.writeByte(i, t++);
}
if (r) {
this.offset = t;
return this;
}
return t - f;
};
r.readBitSet = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
var r, i = this.readVarint32(e), f = i.value, n = f >> 3, s = 0, o = [];
e += i.length;
for (;n--; ) {
r = this.readByte(e++);
o[s++] = !!(1 & r);
o[s++] = !!(2 & r);
o[s++] = !!(4 & r);
o[s++] = !!(8 & r);
o[s++] = !!(16 & r);
o[s++] = !!(32 & r);
o[s++] = !!(64 & r);
o[s++] = !!(128 & r);
}
if (s < f) {
var h = 0;
r = this.readByte(e++);
for (;s < f; ) o[s++] = !!(r >> h++ & 1);
}
t && (this.offset = e);
return o;
};
r.readBytes = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + e > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+" + e + ") <= " + this.buffer.byteLength);
}
var i = this.slice(t, t + e);
r && (this.offset += e);
return i;
};
r.writeBytes = r.append;
r.writeInt8 = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal value: " + e + " (not an integer)");
e |= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
t += 1;
var i = this.buffer.byteLength;
t > i && this.resize((i *= 2) > t ? i : t);
t -= 1;
this.view[t] = e;
r && (this.offset += 1);
return this;
};
r.writeByte = r.writeInt8;
r.readInt8 = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 1 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+1) <= " + this.buffer.byteLength);
}
var r = this.view[e];
128 == (128 & r) && (r = -(255 - r + 1));
t && (this.offset += 1);
return r;
};
r.readByte = r.readInt8;
r.writeUint8 = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal value: " + e + " (not an integer)");
e >>>= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
t += 1;
var i = this.buffer.byteLength;
t > i && this.resize((i *= 2) > t ? i : t);
t -= 1;
this.view[t] = e;
r && (this.offset += 1);
return this;
};
r.writeUInt8 = r.writeUint8;
r.readUint8 = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 1 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+1) <= " + this.buffer.byteLength);
}
var r = this.view[e];
t && (this.offset += 1);
return r;
};
r.readUInt8 = r.readUint8;
r.writeInt16 = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal value: " + e + " (not an integer)");
e |= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
t += 2;
var i = this.buffer.byteLength;
t > i && this.resize((i *= 2) > t ? i : t);
t -= 2;
if (this.littleEndian) {
this.view[t + 1] = (65280 & e) >>> 8;
this.view[t] = 255 & e;
} else {
this.view[t] = (65280 & e) >>> 8;
this.view[t + 1] = 255 & e;
}
r && (this.offset += 2);
return this;
};
r.writeShort = r.writeInt16;
r.readInt16 = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 2 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+2) <= " + this.buffer.byteLength);
}
var r = 0;
if (this.littleEndian) {
r = this.view[e];
r |= this.view[e + 1] << 8;
} else {
r = this.view[e] << 8;
r |= this.view[e + 1];
}
32768 == (32768 & r) && (r = -(65535 - r + 1));
t && (this.offset += 2);
return r;
};
r.readShort = r.readInt16;
r.writeUint16 = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal value: " + e + " (not an integer)");
e >>>= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
t += 2;
var i = this.buffer.byteLength;
t > i && this.resize((i *= 2) > t ? i : t);
t -= 2;
if (this.littleEndian) {
this.view[t + 1] = (65280 & e) >>> 8;
this.view[t] = 255 & e;
} else {
this.view[t] = (65280 & e) >>> 8;
this.view[t + 1] = 255 & e;
}
r && (this.offset += 2);
return this;
};
r.writeUInt16 = r.writeUint16;
r.readUint16 = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 2 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+2) <= " + this.buffer.byteLength);
}
var r = 0;
if (this.littleEndian) {
r = this.view[e];
r |= this.view[e + 1] << 8;
} else {
r = this.view[e] << 8;
r |= this.view[e + 1];
}
t && (this.offset += 2);
return r;
};
r.readUInt16 = r.readUint16;
r.writeInt32 = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal value: " + e + " (not an integer)");
e |= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
t += 4;
var i = this.buffer.byteLength;
t > i && this.resize((i *= 2) > t ? i : t);
t -= 4;
if (this.littleEndian) {
this.view[t + 3] = e >>> 24 & 255;
this.view[t + 2] = e >>> 16 & 255;
this.view[t + 1] = e >>> 8 & 255;
this.view[t] = 255 & e;
} else {
this.view[t] = e >>> 24 & 255;
this.view[t + 1] = e >>> 16 & 255;
this.view[t + 2] = e >>> 8 & 255;
this.view[t + 3] = 255 & e;
}
r && (this.offset += 4);
return this;
};
r.writeInt = r.writeInt32;
r.readInt32 = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 4 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+4) <= " + this.buffer.byteLength);
}
var r = 0;
if (this.littleEndian) {
r = this.view[e + 2] << 16;
r |= this.view[e + 1] << 8;
r |= this.view[e];
r += this.view[e + 3] << 24 >>> 0;
} else {
r = this.view[e + 1] << 16;
r |= this.view[e + 2] << 8;
r |= this.view[e + 3];
r += this.view[e] << 24 >>> 0;
}
r |= 0;
t && (this.offset += 4);
return r;
};
r.readInt = r.readInt32;
r.writeUint32 = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal value: " + e + " (not an integer)");
e >>>= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
t += 4;
var i = this.buffer.byteLength;
t > i && this.resize((i *= 2) > t ? i : t);
t -= 4;
if (this.littleEndian) {
this.view[t + 3] = e >>> 24 & 255;
this.view[t + 2] = e >>> 16 & 255;
this.view[t + 1] = e >>> 8 & 255;
this.view[t] = 255 & e;
} else {
this.view[t] = e >>> 24 & 255;
this.view[t + 1] = e >>> 16 & 255;
this.view[t + 2] = e >>> 8 & 255;
this.view[t + 3] = 255 & e;
}
r && (this.offset += 4);
return this;
};
r.writeUInt32 = r.writeUint32;
r.readUint32 = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 4 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+4) <= " + this.buffer.byteLength);
}
var r = 0;
if (this.littleEndian) {
r = this.view[e + 2] << 16;
r |= this.view[e + 1] << 8;
r |= this.view[e];
r += this.view[e + 3] << 24 >>> 0;
} else {
r = this.view[e + 1] << 16;
r |= this.view[e + 2] << 8;
r |= this.view[e + 3];
r += this.view[e] << 24 >>> 0;
}
t && (this.offset += 4);
return r;
};
r.readUInt32 = r.readUint32;
if (e) {
r.writeInt64 = function(t, r) {
var i = "undefined" == typeof r;
i && (r = this.offset);
if (!this.noAssert) {
if ("number" == typeof t) t = e.fromNumber(t); else if ("string" == typeof t) t = e.fromString(t); else if (!(t && t instanceof e)) throw TypeError("Illegal value: " + t + " (not an integer or Long)");
if ("number" != typeof r || r % 1 != 0) throw TypeError("Illegal offset: " + r + " (not an integer)");
if ((r >>>= 0) < 0 || r + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + r + " (+0) <= " + this.buffer.byteLength);
}
"number" == typeof t ? t = e.fromNumber(t) : "string" == typeof t && (t = e.fromString(t));
r += 8;
var f = this.buffer.byteLength;
r > f && this.resize((f *= 2) > r ? f : r);
r -= 8;
var n = t.low, s = t.high;
if (this.littleEndian) {
this.view[r + 3] = n >>> 24 & 255;
this.view[r + 2] = n >>> 16 & 255;
this.view[r + 1] = n >>> 8 & 255;
this.view[r] = 255 & n;
r += 4;
this.view[r + 3] = s >>> 24 & 255;
this.view[r + 2] = s >>> 16 & 255;
this.view[r + 1] = s >>> 8 & 255;
this.view[r] = 255 & s;
} else {
this.view[r] = s >>> 24 & 255;
this.view[r + 1] = s >>> 16 & 255;
this.view[r + 2] = s >>> 8 & 255;
this.view[r + 3] = 255 & s;
r += 4;
this.view[r] = n >>> 24 & 255;
this.view[r + 1] = n >>> 16 & 255;
this.view[r + 2] = n >>> 8 & 255;
this.view[r + 3] = 255 & n;
}
i && (this.offset += 8);
return this;
};
r.writeLong = r.writeInt64;
r.readInt64 = function(t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 8 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+8) <= " + this.buffer.byteLength);
}
var i = 0, f = 0;
if (this.littleEndian) {
i = this.view[t + 2] << 16;
i |= this.view[t + 1] << 8;
i |= this.view[t];
i += this.view[t + 3] << 24 >>> 0;
t += 4;
f = this.view[t + 2] << 16;
f |= this.view[t + 1] << 8;
f |= this.view[t];
f += this.view[t + 3] << 24 >>> 0;
} else {
f = this.view[t + 1] << 16;
f |= this.view[t + 2] << 8;
f |= this.view[t + 3];
f += this.view[t] << 24 >>> 0;
t += 4;
i = this.view[t + 1] << 16;
i |= this.view[t + 2] << 8;
i |= this.view[t + 3];
i += this.view[t] << 24 >>> 0;
}
var n = new e(i, f, !1);
r && (this.offset += 8);
return n;
};
r.readLong = r.readInt64;
r.writeUint64 = function(t, r) {
var i = "undefined" == typeof r;
i && (r = this.offset);
if (!this.noAssert) {
if ("number" == typeof t) t = e.fromNumber(t); else if ("string" == typeof t) t = e.fromString(t); else if (!(t && t instanceof e)) throw TypeError("Illegal value: " + t + " (not an integer or Long)");
if ("number" != typeof r || r % 1 != 0) throw TypeError("Illegal offset: " + r + " (not an integer)");
if ((r >>>= 0) < 0 || r + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + r + " (+0) <= " + this.buffer.byteLength);
}
"number" == typeof t ? t = e.fromNumber(t) : "string" == typeof t && (t = e.fromString(t));
r += 8;
var f = this.buffer.byteLength;
r > f && this.resize((f *= 2) > r ? f : r);
r -= 8;
var n = t.low, s = t.high;
if (this.littleEndian) {
this.view[r + 3] = n >>> 24 & 255;
this.view[r + 2] = n >>> 16 & 255;
this.view[r + 1] = n >>> 8 & 255;
this.view[r] = 255 & n;
r += 4;
this.view[r + 3] = s >>> 24 & 255;
this.view[r + 2] = s >>> 16 & 255;
this.view[r + 1] = s >>> 8 & 255;
this.view[r] = 255 & s;
} else {
this.view[r] = s >>> 24 & 255;
this.view[r + 1] = s >>> 16 & 255;
this.view[r + 2] = s >>> 8 & 255;
this.view[r + 3] = 255 & s;
r += 4;
this.view[r] = n >>> 24 & 255;
this.view[r + 1] = n >>> 16 & 255;
this.view[r + 2] = n >>> 8 & 255;
this.view[r + 3] = 255 & n;
}
i && (this.offset += 8);
return this;
};
r.writeUInt64 = r.writeUint64;
r.readUint64 = function(t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 8 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+8) <= " + this.buffer.byteLength);
}
var i = 0, f = 0;
if (this.littleEndian) {
i = this.view[t + 2] << 16;
i |= this.view[t + 1] << 8;
i |= this.view[t];
i += this.view[t + 3] << 24 >>> 0;
t += 4;
f = this.view[t + 2] << 16;
f |= this.view[t + 1] << 8;
f |= this.view[t];
f += this.view[t + 3] << 24 >>> 0;
} else {
f = this.view[t + 1] << 16;
f |= this.view[t + 2] << 8;
f |= this.view[t + 3];
f += this.view[t] << 24 >>> 0;
t += 4;
i = this.view[t + 1] << 16;
i |= this.view[t + 2] << 8;
i |= this.view[t + 3];
i += this.view[t] << 24 >>> 0;
}
var n = new e(i, f, !0);
r && (this.offset += 8);
return n;
};
r.readUInt64 = r.readUint64;
}
function o(e, t, r, i, f) {
var n, s, o = 8 * f - i - 1, h = (1 << o) - 1, a = h >> 1, l = -7, u = r ? f - 1 : 0, g = r ? -1 : 1, y = e[t + u];
u += g;
n = y & (1 << -l) - 1;
y >>= -l;
l += o;
for (;l > 0; n = 256 * n + e[t + u], u += g, l -= 8) ;
s = n & (1 << -l) - 1;
n >>= -l;
l += i;
for (;l > 0; s = 256 * s + e[t + u], u += g, l -= 8) ;
if (0 === n) n = 1 - a; else {
if (n === h) return s ? NaN : Infinity * (y ? -1 : 1);
s += Math.pow(2, i);
n -= a;
}
return (y ? -1 : 1) * s * Math.pow(2, n - i);
}
function h(e, t, r, i, f, n) {
var s, o, h, a = 8 * n - f - 1, l = (1 << a) - 1, u = l >> 1, g = 23 === f ? Math.pow(2, -24) - Math.pow(2, -77) : 0, y = i ? 0 : n - 1, w = i ? 1 : -1, b = t < 0 || 0 === t && 1 / t < 0 ? 1 : 0;
t = Math.abs(t);
if (isNaN(t) || Infinity === t) {
o = isNaN(t) ? 1 : 0;
s = l;
} else {
s = Math.floor(Math.log(t) / Math.LN2);
if (t * (h = Math.pow(2, -s)) < 1) {
s--;
h *= 2;
}
if ((t += s + u >= 1 ? g / h : g * Math.pow(2, 1 - u)) * h >= 2) {
s++;
h /= 2;
}
if (s + u >= l) {
o = 0;
s = l;
} else if (s + u >= 1) {
o = (t * h - 1) * Math.pow(2, f);
s += u;
} else {
o = t * Math.pow(2, u - 1) * Math.pow(2, f);
s = 0;
}
}
for (;f >= 8; e[r + y] = 255 & o, y += w, o /= 256, f -= 8) ;
s = s << f | o;
a += f;
for (;a > 0; e[r + y] = 255 & s, y += w, s /= 256, a -= 8) ;
e[r + y - w] |= 128 * b;
}
r.writeFloat32 = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof e) throw TypeError("Illegal value: " + e + " (not a number)");
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
t += 4;
var i = this.buffer.byteLength;
t > i && this.resize((i *= 2) > t ? i : t);
t -= 4;
h(this.view, e, t, this.littleEndian, 23, 4);
r && (this.offset += 4);
return this;
};
r.writeFloat = r.writeFloat32;
r.readFloat32 = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 4 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+4) <= " + this.buffer.byteLength);
}
var r = o(this.view, e, this.littleEndian, 23, 4);
t && (this.offset += 4);
return r;
};
r.readFloat = r.readFloat32;
r.writeFloat64 = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof e) throw TypeError("Illegal value: " + e + " (not a number)");
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
t += 8;
var i = this.buffer.byteLength;
t > i && this.resize((i *= 2) > t ? i : t);
t -= 8;
h(this.view, e, t, this.littleEndian, 52, 8);
r && (this.offset += 8);
return this;
};
r.writeDouble = r.writeFloat64;
r.readFloat64 = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 8 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+8) <= " + this.buffer.byteLength);
}
var r = o(this.view, e, this.littleEndian, 52, 8);
t && (this.offset += 8);
return r;
};
r.readDouble = r.readFloat64;
t.MAX_VARINT32_BYTES = 5;
t.calculateVarint32 = function(e) {
return (e >>>= 0) < 128 ? 1 : e < 16384 ? 2 : e < 1 << 21 ? 3 : e < 1 << 28 ? 4 : 5;
};
t.zigZagEncode32 = function(e) {
return ((e |= 0) << 1 ^ e >> 31) >>> 0;
};
t.zigZagDecode32 = function(e) {
return e >>> 1 ^ -(1 & e) | 0;
};
r.writeVarint32 = function(e, r) {
var i = "undefined" == typeof r;
i && (r = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal value: " + e + " (not an integer)");
e |= 0;
if ("number" != typeof r || r % 1 != 0) throw TypeError("Illegal offset: " + r + " (not an integer)");
if ((r >>>= 0) < 0 || r + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + r + " (+0) <= " + this.buffer.byteLength);
}
var f, n = t.calculateVarint32(e);
r += n;
var s = this.buffer.byteLength;
r > s && this.resize((s *= 2) > r ? s : r);
r -= n;
e >>>= 0;
for (;e >= 128; ) {
f = 127 & e | 128;
this.view[r++] = f;
e >>>= 7;
}
this.view[r++] = e;
if (i) {
this.offset = r;
return this;
}
return n;
};
r.writeVarint32ZigZag = function(e, r) {
return this.writeVarint32(t.zigZagEncode32(e), r);
};
r.readVarint32 = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 1 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+1) <= " + this.buffer.byteLength);
}
var r, i = 0, f = 0;
do {
if (!this.noAssert && e > this.limit) {
var n = Error("Truncated");
n.truncated = !0;
throw n;
}
r = this.view[e++];
i < 5 && (f |= (127 & r) << 7 * i);
++i;
} while (0 != (128 & r));
f |= 0;
if (t) {
this.offset = e;
return f;
}
return {
value: f,
length: i
};
};
r.readVarint32ZigZag = function(e) {
var r = this.readVarint32(e);
"object" == typeof r ? r.value = t.zigZagDecode32(r.value) : r = t.zigZagDecode32(r);
return r;
};
if (e) {
t.MAX_VARINT64_BYTES = 10;
t.calculateVarint64 = function(t) {
"number" == typeof t ? t = e.fromNumber(t) : "string" == typeof t && (t = e.fromString(t));
var r = t.toInt() >>> 0, i = t.shiftRightUnsigned(28).toInt() >>> 0, f = t.shiftRightUnsigned(56).toInt() >>> 0;
return 0 == f ? 0 == i ? r < 16384 ? r < 128 ? 1 : 2 : r < 1 << 21 ? 3 : 4 : i < 16384 ? i < 128 ? 5 : 6 : i < 1 << 21 ? 7 : 8 : f < 128 ? 9 : 10;
};
t.zigZagEncode64 = function(t) {
"number" == typeof t ? t = e.fromNumber(t, !1) : "string" == typeof t ? t = e.fromString(t, !1) : !1 !== t.unsigned && (t = t.toSigned());
return t.shiftLeft(1).xor(t.shiftRight(63)).toUnsigned();
};
t.zigZagDecode64 = function(t) {
"number" == typeof t ? t = e.fromNumber(t, !1) : "string" == typeof t ? t = e.fromString(t, !1) : !1 !== t.unsigned && (t = t.toSigned());
return t.shiftRightUnsigned(1).xor(t.and(e.ONE).toSigned().negate()).toSigned();
};
r.writeVarint64 = function(r, i) {
var f = "undefined" == typeof i;
f && (i = this.offset);
if (!this.noAssert) {
if ("number" == typeof r) r = e.fromNumber(r); else if ("string" == typeof r) r = e.fromString(r); else if (!(r && r instanceof e)) throw TypeError("Illegal value: " + r + " (not an integer or Long)");
if ("number" != typeof i || i % 1 != 0) throw TypeError("Illegal offset: " + i + " (not an integer)");
if ((i >>>= 0) < 0 || i + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + i + " (+0) <= " + this.buffer.byteLength);
}
"number" == typeof r ? r = e.fromNumber(r, !1) : "string" == typeof r ? r = e.fromString(r, !1) : !1 !== r.unsigned && (r = r.toSigned());
var n = t.calculateVarint64(r), s = r.toInt() >>> 0, o = r.shiftRightUnsigned(28).toInt() >>> 0, h = r.shiftRightUnsigned(56).toInt() >>> 0;
i += n;
var a = this.buffer.byteLength;
i > a && this.resize((a *= 2) > i ? a : i);
i -= n;
switch (n) {
case 10:
this.view[i + 9] = h >>> 7 & 1;

case 9:
this.view[i + 8] = 9 !== n ? 128 | h : 127 & h;

case 8:
this.view[i + 7] = 8 !== n ? o >>> 21 | 128 : o >>> 21 & 127;

case 7:
this.view[i + 6] = 7 !== n ? o >>> 14 | 128 : o >>> 14 & 127;

case 6:
this.view[i + 5] = 6 !== n ? o >>> 7 | 128 : o >>> 7 & 127;

case 5:
this.view[i + 4] = 5 !== n ? 128 | o : 127 & o;

case 4:
this.view[i + 3] = 4 !== n ? s >>> 21 | 128 : s >>> 21 & 127;

case 3:
this.view[i + 2] = 3 !== n ? s >>> 14 | 128 : s >>> 14 & 127;

case 2:
this.view[i + 1] = 2 !== n ? s >>> 7 | 128 : s >>> 7 & 127;

case 1:
this.view[i] = 1 !== n ? 128 | s : 127 & s;
}
if (f) {
this.offset += n;
return this;
}
return n;
};
r.writeVarint64ZigZag = function(e, r) {
return this.writeVarint64(t.zigZagEncode64(e), r);
};
r.readVarint64 = function(t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 1 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+1) <= " + this.buffer.byteLength);
}
var i = t, f = 0, n = 0, s = 0, o = 0;
f = 127 & (o = this.view[t++]);
if (128 & o) {
f |= (127 & (o = this.view[t++])) << 7;
if (128 & o || this.noAssert && "undefined" == typeof o) {
f |= (127 & (o = this.view[t++])) << 14;
if (128 & o || this.noAssert && "undefined" == typeof o) {
f |= (127 & (o = this.view[t++])) << 21;
if (128 & o || this.noAssert && "undefined" == typeof o) {
n = 127 & (o = this.view[t++]);
if (128 & o || this.noAssert && "undefined" == typeof o) {
n |= (127 & (o = this.view[t++])) << 7;
if (128 & o || this.noAssert && "undefined" == typeof o) {
n |= (127 & (o = this.view[t++])) << 14;
if (128 & o || this.noAssert && "undefined" == typeof o) {
n |= (127 & (o = this.view[t++])) << 21;
if (128 & o || this.noAssert && "undefined" == typeof o) {
s = 127 & (o = this.view[t++]);
if (128 & o || this.noAssert && "undefined" == typeof o) {
s |= (127 & (o = this.view[t++])) << 7;
if (128 & o || this.noAssert && "undefined" == typeof o) throw Error("Buffer overrun");
}
}
}
}
}
}
}
}
}
var h = e.fromBits(f | n << 28, n >>> 4 | s << 24, !1);
if (r) {
this.offset = t;
return h;
}
return {
value: h,
length: t - i
};
};
r.readVarint64ZigZag = function(r) {
var i = this.readVarint64(r);
i && i.value instanceof e ? i.value = t.zigZagDecode64(i.value) : i = t.zigZagDecode64(i);
return i;
};
}
r.writeCString = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
var i, f = e.length;
if (!this.noAssert) {
if ("string" != typeof e) throw TypeError("Illegal str: Not a string");
for (i = 0; i < f; ++i) if (0 === e.charCodeAt(i)) throw RangeError("Illegal str: Contains NULL-characters");
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
f = l.calculateUTF16asUTF8(n(e))[1];
t += f + 1;
var s = this.buffer.byteLength;
t > s && this.resize((s *= 2) > t ? s : t);
t -= f + 1;
l.encodeUTF16toUTF8(n(e), function(e) {
this.view[t++] = e;
}.bind(this));
this.view[t++] = 0;
if (r) {
this.offset = t;
return this;
}
return f;
};
r.readCString = function(e) {
var t = "undefined" == typeof e;
t && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 1 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+1) <= " + this.buffer.byteLength);
}
var r, i = e, f = -1;
l.decodeUTF8toUTF16(function() {
if (0 === f) return null;
if (e >= this.limit) throw RangeError("Illegal range: Truncated data, " + e + " < " + this.limit);
return 0 === (f = this.view[e++]) ? null : f;
}.bind(this), r = s(), !0);
if (t) {
this.offset = e;
return r();
}
return {
string: r(),
length: e - i
};
};
r.writeIString = function(e, t) {
var r = "undefined" == typeof t;
r && (t = this.offset);
if (!this.noAssert) {
if ("string" != typeof e) throw TypeError("Illegal str: Not a string");
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
var i, f = t;
i = l.calculateUTF16asUTF8(n(e), this.noAssert)[1];
t += 4 + i;
var s = this.buffer.byteLength;
t > s && this.resize((s *= 2) > t ? s : t);
t -= 4 + i;
if (this.littleEndian) {
this.view[t + 3] = i >>> 24 & 255;
this.view[t + 2] = i >>> 16 & 255;
this.view[t + 1] = i >>> 8 & 255;
this.view[t] = 255 & i;
} else {
this.view[t] = i >>> 24 & 255;
this.view[t + 1] = i >>> 16 & 255;
this.view[t + 2] = i >>> 8 & 255;
this.view[t + 3] = 255 & i;
}
t += 4;
l.encodeUTF16toUTF8(n(e), function(e) {
this.view[t++] = e;
}.bind(this));
if (t !== f + 4 + i) throw RangeError("Illegal range: Truncated data, " + t + " == " + (t + 4 + i));
if (r) {
this.offset = t;
return this;
}
return t - f;
};
r.readIString = function(e) {
var r = "undefined" == typeof e;
r && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 4 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+4) <= " + this.buffer.byteLength);
}
var i = e, f = this.readUint32(e), n = this.readUTF8String(f, t.METRICS_BYTES, e += 4);
e += n.length;
if (r) {
this.offset = e;
return n.string;
}
return {
string: n.string,
length: e - i
};
};
t.METRICS_CHARS = "c";
t.METRICS_BYTES = "b";
r.writeUTF8String = function(e, t) {
var r, i = "undefined" == typeof t;
i && (t = this.offset);
if (!this.noAssert) {
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: " + t + " (not an integer)");
if ((t >>>= 0) < 0 || t + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + t + " (+0) <= " + this.buffer.byteLength);
}
var f = t;
r = l.calculateUTF16asUTF8(n(e))[1];
t += r;
var s = this.buffer.byteLength;
t > s && this.resize((s *= 2) > t ? s : t);
t -= r;
l.encodeUTF16toUTF8(n(e), function(e) {
this.view[t++] = e;
}.bind(this));
if (i) {
this.offset = t;
return this;
}
return t - f;
};
r.writeString = r.writeUTF8String;
t.calculateUTF8Chars = function(e) {
return l.calculateUTF16asUTF8(n(e))[0];
};
t.calculateUTF8Bytes = function(e) {
return l.calculateUTF16asUTF8(n(e))[1];
};
t.calculateString = t.calculateUTF8Bytes;
r.readUTF8String = function(e, r, i) {
if ("number" == typeof r) {
i = r;
r = void 0;
}
var f = "undefined" == typeof i;
f && (i = this.offset);
"undefined" == typeof r && (r = t.METRICS_CHARS);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal length: " + e + " (not an integer)");
e |= 0;
if ("number" != typeof i || i % 1 != 0) throw TypeError("Illegal offset: " + i + " (not an integer)");
if ((i >>>= 0) < 0 || i + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + i + " (+0) <= " + this.buffer.byteLength);
}
var n, o = 0, h = i;
if (r === t.METRICS_CHARS) {
n = s();
l.decodeUTF8(function() {
return o < e && i < this.limit ? this.view[i++] : null;
}.bind(this), function(e) {
++o;
l.UTF8toUTF16(e, n);
});
if (o !== e) throw RangeError("Illegal range: Truncated data, " + o + " == " + e);
if (f) {
this.offset = i;
return n();
}
return {
string: n(),
length: i - h
};
}
if (r === t.METRICS_BYTES) {
if (!this.noAssert) {
if ("number" != typeof i || i % 1 != 0) throw TypeError("Illegal offset: " + i + " (not an integer)");
if ((i >>>= 0) < 0 || i + e > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + i + " (+" + e + ") <= " + this.buffer.byteLength);
}
var a = i + e;
l.decodeUTF8toUTF16(function() {
return i < a ? this.view[i++] : null;
}.bind(this), n = s(), this.noAssert);
if (i !== a) throw RangeError("Illegal range: Truncated data, " + i + " == " + a);
if (f) {
this.offset = i;
return n();
}
return {
string: n(),
length: i - h
};
}
throw TypeError("Unsupported metrics: " + r);
};
r.readString = r.readUTF8String;
r.writeVString = function(e, r) {
var i = "undefined" == typeof r;
i && (r = this.offset);
if (!this.noAssert) {
if ("string" != typeof e) throw TypeError("Illegal str: Not a string");
if ("number" != typeof r || r % 1 != 0) throw TypeError("Illegal offset: " + r + " (not an integer)");
if ((r >>>= 0) < 0 || r + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + r + " (+0) <= " + this.buffer.byteLength);
}
var f, s, o = r;
f = l.calculateUTF16asUTF8(n(e), this.noAssert)[1];
s = t.calculateVarint32(f);
r += s + f;
var h = this.buffer.byteLength;
r > h && this.resize((h *= 2) > r ? h : r);
r -= s + f;
r += this.writeVarint32(f, r);
l.encodeUTF16toUTF8(n(e), function(e) {
this.view[r++] = e;
}.bind(this));
if (r !== o + f + s) throw RangeError("Illegal range: Truncated data, " + r + " == " + (r + f + s));
if (i) {
this.offset = r;
return this;
}
return r - o;
};
r.readVString = function(e) {
var r = "undefined" == typeof e;
r && (e = this.offset);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 1 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+1) <= " + this.buffer.byteLength);
}
var i = e, f = this.readVarint32(e), n = this.readUTF8String(f.value, t.METRICS_BYTES, e += f.length);
e += n.length;
if (r) {
this.offset = e;
return n.string;
}
return {
string: n.string,
length: e - i
};
};
r.append = function(e, r, i) {
if ("number" == typeof r || "string" != typeof r) {
i = r;
r = void 0;
}
var f = "undefined" == typeof i;
f && (i = this.offset);
if (!this.noAssert) {
if ("number" != typeof i || i % 1 != 0) throw TypeError("Illegal offset: " + i + " (not an integer)");
if ((i >>>= 0) < 0 || i + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + i + " (+0) <= " + this.buffer.byteLength);
}
e instanceof t || (e = t.wrap(e, r));
var n = e.limit - e.offset;
if (n <= 0) return this;
i += n;
var s = this.buffer.byteLength;
i > s && this.resize((s *= 2) > i ? s : i);
i -= n;
this.view.set(e.view.subarray(e.offset, e.limit), i);
e.offset += n;
f && (this.offset += n);
return this;
};
r.appendTo = function(e, t) {
e.append(this, t);
return this;
};
r.assert = function(e) {
this.noAssert = !e;
return this;
};
r.capacity = function() {
return this.buffer.byteLength;
};
r.clear = function() {
this.offset = 0;
this.limit = this.buffer.byteLength;
this.markedOffset = -1;
return this;
};
r.clone = function(e) {
var r = new t(0, this.littleEndian, this.noAssert);
if (e) {
r.buffer = new ArrayBuffer(this.buffer.byteLength);
r.view = new Uint8Array(r.buffer);
} else {
r.buffer = this.buffer;
r.view = this.view;
}
r.offset = this.offset;
r.markedOffset = this.markedOffset;
r.limit = this.limit;
return r;
};
r.compact = function(e, t) {
"undefined" == typeof e && (e = this.offset);
"undefined" == typeof t && (t = this.limit);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal begin: Not an integer");
e >>>= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal end: Not an integer");
t >>>= 0;
if (e < 0 || e > t || t > this.buffer.byteLength) throw RangeError("Illegal range: 0 <= " + e + " <= " + t + " <= " + this.buffer.byteLength);
}
if (0 === e && t === this.buffer.byteLength) return this;
var r = t - e;
if (0 === r) {
this.buffer = i;
this.view = null;
this.markedOffset >= 0 && (this.markedOffset -= e);
this.offset = 0;
this.limit = 0;
return this;
}
var f = new ArrayBuffer(r), n = new Uint8Array(f);
n.set(this.view.subarray(e, t));
this.buffer = f;
this.view = n;
this.markedOffset >= 0 && (this.markedOffset -= e);
this.offset = 0;
this.limit = r;
return this;
};
r.copy = function(e, r) {
"undefined" == typeof e && (e = this.offset);
"undefined" == typeof r && (r = this.limit);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal begin: Not an integer");
e >>>= 0;
if ("number" != typeof r || r % 1 != 0) throw TypeError("Illegal end: Not an integer");
r >>>= 0;
if (e < 0 || e > r || r > this.buffer.byteLength) throw RangeError("Illegal range: 0 <= " + e + " <= " + r + " <= " + this.buffer.byteLength);
}
if (e === r) return new t(0, this.littleEndian, this.noAssert);
var i = r - e, f = new t(i, this.littleEndian, this.noAssert);
f.offset = 0;
f.limit = i;
f.markedOffset >= 0 && (f.markedOffset -= e);
this.copyTo(f, 0, e, r);
return f;
};
r.copyTo = function(e, r, i, f) {
var n, s;
if (!this.noAssert && !t.isByteBuffer(e)) throw TypeError("Illegal target: Not a ByteBuffer");
r = (s = "undefined" == typeof r) ? e.offset : 0 | r;
i = (n = "undefined" == typeof i) ? this.offset : 0 | i;
f = "undefined" == typeof f ? this.limit : 0 | f;
if (r < 0 || r > e.buffer.byteLength) throw RangeError("Illegal target range: 0 <= " + r + " <= " + e.buffer.byteLength);
if (i < 0 || f > this.buffer.byteLength) throw RangeError("Illegal source range: 0 <= " + i + " <= " + this.buffer.byteLength);
var o = f - i;
if (0 === o) return e;
e.ensureCapacity(r + o);
e.view.set(this.view.subarray(i, f), r);
n && (this.offset += o);
s && (e.offset += o);
return this;
};
r.ensureCapacity = function(e) {
var t = this.buffer.byteLength;
return t < e ? this.resize((t *= 2) > e ? t : e) : this;
};
r.fill = function(e, t, r) {
var i = "undefined" == typeof t;
i && (t = this.offset);
"string" == typeof e && e.length > 0 && (e = e.charCodeAt(0));
"undefined" == typeof t && (t = this.offset);
"undefined" == typeof r && (r = this.limit);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal value: " + e + " (not an integer)");
e |= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal begin: Not an integer");
t >>>= 0;
if ("number" != typeof r || r % 1 != 0) throw TypeError("Illegal end: Not an integer");
r >>>= 0;
if (t < 0 || t > r || r > this.buffer.byteLength) throw RangeError("Illegal range: 0 <= " + t + " <= " + r + " <= " + this.buffer.byteLength);
}
if (t >= r) return this;
for (;t < r; ) this.view[t++] = e;
i && (this.offset = t);
return this;
};
r.flip = function() {
this.limit = this.offset;
this.offset = 0;
return this;
};
r.mark = function(e) {
e = "undefined" == typeof e ? this.offset : e;
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal offset: " + e + " (not an integer)");
if ((e >>>= 0) < 0 || e + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + e + " (+0) <= " + this.buffer.byteLength);
}
this.markedOffset = e;
return this;
};
r.order = function(e) {
if (!this.noAssert && "boolean" != typeof e) throw TypeError("Illegal littleEndian: Not a boolean");
this.littleEndian = !!e;
return this;
};
r.LE = function(e) {
this.littleEndian = "undefined" == typeof e || !!e;
return this;
};
r.BE = function(e) {
this.littleEndian = "undefined" != typeof e && !e;
return this;
};
r.prepend = function(e, r, i) {
if ("number" == typeof r || "string" != typeof r) {
i = r;
r = void 0;
}
var f = "undefined" == typeof i;
f && (i = this.offset);
if (!this.noAssert) {
if ("number" != typeof i || i % 1 != 0) throw TypeError("Illegal offset: " + i + " (not an integer)");
if ((i >>>= 0) < 0 || i + 0 > this.buffer.byteLength) throw RangeError("Illegal offset: 0 <= " + i + " (+0) <= " + this.buffer.byteLength);
}
e instanceof t || (e = t.wrap(e, r));
var n = e.limit - e.offset;
if (n <= 0) return this;
var s = n - i;
if (s > 0) {
var o = new ArrayBuffer(this.buffer.byteLength + s), h = new Uint8Array(o);
h.set(this.view.subarray(i, this.buffer.byteLength), n);
this.buffer = o;
this.view = h;
this.offset += s;
this.markedOffset >= 0 && (this.markedOffset += s);
this.limit += s;
i += s;
} else new Uint8Array(this.buffer);
this.view.set(e.view.subarray(e.offset, e.limit), i - n);
e.offset = e.limit;
f && (this.offset -= n);
return this;
};
r.prependTo = function(e, t) {
e.prepend(this, t);
return this;
};
r.printDebug = function(e) {
"function" != typeof e && (e = console.log.bind(console));
e(this.toString() + "\n-------------------------------------------------------------------\n" + this.toDebug(!0));
};
r.remaining = function() {
return this.limit - this.offset;
};
r.reset = function() {
if (this.markedOffset >= 0) {
this.offset = this.markedOffset;
this.markedOffset = -1;
} else this.offset = 0;
return this;
};
r.resize = function(e) {
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal capacity: " + e + " (not an integer)");
if ((e |= 0) < 0) throw RangeError("Illegal capacity: 0 <= " + e);
}
if (this.buffer.byteLength < e) {
var t = new ArrayBuffer(e), r = new Uint8Array(t);
r.set(this.view);
this.buffer = t;
this.view = r;
}
return this;
};
r.reverse = function(e, t) {
"undefined" == typeof e && (e = this.offset);
"undefined" == typeof t && (t = this.limit);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal begin: Not an integer");
e >>>= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal end: Not an integer");
t >>>= 0;
if (e < 0 || e > t || t > this.buffer.byteLength) throw RangeError("Illegal range: 0 <= " + e + " <= " + t + " <= " + this.buffer.byteLength);
}
if (e === t) return this;
Array.prototype.reverse.call(this.view.subarray(e, t));
return this;
};
r.skip = function(e) {
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal length: " + e + " (not an integer)");
e |= 0;
}
var t = this.offset + e;
if (!this.noAssert && (t < 0 || t > this.buffer.byteLength)) throw RangeError("Illegal length: 0 <= " + this.offset + " + " + e + " <= " + this.buffer.byteLength);
this.offset = t;
return this;
};
r.slice = function(e, t) {
"undefined" == typeof e && (e = this.offset);
"undefined" == typeof t && (t = this.limit);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal begin: Not an integer");
e >>>= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal end: Not an integer");
t >>>= 0;
if (e < 0 || e > t || t > this.buffer.byteLength) throw RangeError("Illegal range: 0 <= " + e + " <= " + t + " <= " + this.buffer.byteLength);
}
var r = this.clone();
r.offset = e;
r.limit = t;
return r;
};
r.toBuffer = function(e) {
var t = this.offset, r = this.limit;
if (!this.noAssert) {
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal offset: Not an integer");
t >>>= 0;
if ("number" != typeof r || r % 1 != 0) throw TypeError("Illegal limit: Not an integer");
r >>>= 0;
if (t < 0 || t > r || r > this.buffer.byteLength) throw RangeError("Illegal range: 0 <= " + t + " <= " + r + " <= " + this.buffer.byteLength);
}
if (!e && 0 === t && r === this.buffer.byteLength) return this.buffer;
if (t === r) return i;
var f = new ArrayBuffer(r - t);
new Uint8Array(f).set(new Uint8Array(this.buffer).subarray(t, r), 0);
return f;
};
r.toArrayBuffer = r.toBuffer;
r.toString = function(e, t, r) {
if ("undefined" == typeof e) return "ByteBufferAB(offset=" + this.offset + ",markedOffset=" + this.markedOffset + ",limit=" + this.limit + ",capacity=" + this.capacity() + ")";
"number" == typeof e && (r = t = e = "utf8");
switch (e) {
case "utf8":
return this.toUTF8(t, r);

case "base64":
return this.toBase64(t, r);

case "hex":
return this.toHex(t, r);

case "binary":
return this.toBinary(t, r);

case "debug":
return this.toDebug();

case "columns":
return this.toColumns();

default:
throw Error("Unsupported encoding: " + e);
}
};
var a = function() {
for (var e = {}, t = [ 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 ], r = [], i = 0, f = t.length; i < f; ++i) r[t[i]] = i;
e.encode = function(e, r) {
for (var i, f; null !== (i = e()); ) {
r(t[i >> 2 & 63]);
f = (3 & i) << 4;
if (null !== (i = e())) {
r(t[63 & ((f |= i >> 4 & 15) | i >> 4 & 15)]);
f = (15 & i) << 2;
null !== (i = e()) ? (r(t[63 & (f | i >> 6 & 3)]), r(t[63 & i])) : (r(t[63 & f]), 
r(61));
} else r(t[63 & f]), r(61), r(61);
}
};
e.decode = function(e, t) {
var i, f, n;
function s(e) {
throw Error("Illegal character code: " + e);
}
for (;null !== (i = e()); ) {
"undefined" == typeof (f = r[i]) && s(i);
if (null !== (i = e())) {
"undefined" == typeof (n = r[i]) && s(i);
t(f << 2 >>> 0 | (48 & n) >> 4);
if (null !== (i = e())) {
if ("undefined" == typeof (f = r[i])) {
if (61 === i) break;
s(i);
}
t((15 & n) << 4 >>> 0 | (60 & f) >> 2);
if (null !== (i = e())) {
if ("undefined" == typeof (n = r[i])) {
if (61 === i) break;
s(i);
}
t((3 & f) << 6 >>> 0 | n);
}
}
}
}
};
e.test = function(e) {
return /^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$/.test(e);
};
return e;
}();
r.toBase64 = function(e, t) {
"undefined" == typeof e && (e = this.offset);
"undefined" == typeof t && (t = this.limit);
t |= 0;
if ((e |= 0) < 0 || t > this.capacity || e > t) throw RangeError("begin, end");
var r;
a.encode(function() {
return e < t ? this.view[e++] : null;
}.bind(this), r = s());
return r();
};
t.fromBase64 = function(e, r) {
if ("string" != typeof e) throw TypeError("str");
var i = new t(e.length / 4 * 3, r), f = 0;
a.decode(n(e), function(e) {
i.view[f++] = e;
});
i.limit = f;
return i;
};
t.btoa = function(e) {
return t.fromBinary(e).toBase64();
};
t.atob = function(e) {
return t.fromBase64(e).toBinary();
};
r.toBinary = function(e, t) {
"undefined" == typeof e && (e = this.offset);
"undefined" == typeof t && (t = this.limit);
t |= 0;
if ((e |= 0) < 0 || t > this.capacity() || e > t) throw RangeError("begin, end");
if (e === t) return "";
for (var r = [], i = []; e < t; ) {
r.push(this.view[e++]);
r.length >= 1024 && (i.push(String.fromCharCode.apply(String, r)), r = []);
}
return i.join("") + String.fromCharCode.apply(String, r);
};
t.fromBinary = function(e, r) {
if ("string" != typeof e) throw TypeError("str");
for (var i, f = 0, n = e.length, s = new t(n, r); f < n; ) {
if ((i = e.charCodeAt(f)) > 255) throw RangeError("illegal char code: " + i);
s.view[f++] = i;
}
s.limit = n;
return s;
};
r.toDebug = function(e) {
for (var t, r = -1, i = this.buffer.byteLength, f = "", n = "", s = ""; r < i; ) {
if (-1 !== r) {
f += (t = this.view[r]) < 16 ? "0" + t.toString(16).toUpperCase() : t.toString(16).toUpperCase();
e && (n += t > 32 && t < 127 ? String.fromCharCode(t) : ".");
}
++r;
if (e && r > 0 && r % 16 == 0 && r !== i) {
for (;f.length < 51; ) f += " ";
s += f + n + "\n";
f = n = "";
}
r === this.offset && r === this.limit ? f += r === this.markedOffset ? "!" : "|" : r === this.offset ? f += r === this.markedOffset ? "[" : "<" : r === this.limit ? f += r === this.markedOffset ? "]" : ">" : f += r === this.markedOffset ? "'" : e || 0 !== r && r !== i ? " " : "";
}
if (e && " " !== f) {
for (;f.length < 51; ) f += " ";
s += f + n + "\n";
}
return e ? s : f;
};
t.fromDebug = function(e, r, i) {
for (var f, n, s = e.length, o = new t((s + 1) / 3 | 0, r, i), h = 0, a = 0, l = !1, u = !1, g = !1, y = !1, w = !1; h < s; ) {
switch (f = e.charAt(h++)) {
case "!":
if (!i) {
if (u || g || y) {
w = !0;
break;
}
u = g = y = !0;
}
o.offset = o.markedOffset = o.limit = a;
l = !1;
break;

case "|":
if (!i) {
if (u || y) {
w = !0;
break;
}
u = y = !0;
}
o.offset = o.limit = a;
l = !1;
break;

case "[":
if (!i) {
if (u || g) {
w = !0;
break;
}
u = g = !0;
}
o.offset = o.markedOffset = a;
l = !1;
break;

case "<":
if (!i) {
if (u) {
w = !0;
break;
}
u = !0;
}
o.offset = a;
l = !1;
break;

case "]":
if (!i) {
if (y || g) {
w = !0;
break;
}
y = g = !0;
}
o.limit = o.markedOffset = a;
l = !1;
break;

case ">":
if (!i) {
if (y) {
w = !0;
break;
}
y = !0;
}
o.limit = a;
l = !1;
break;

case "'":
if (!i) {
if (g) {
w = !0;
break;
}
g = !0;
}
o.markedOffset = a;
l = !1;
break;

case " ":
l = !1;
break;

default:
if (!i && l) {
w = !0;
break;
}
n = parseInt(f + e.charAt(h++), 16);
if (!i && (isNaN(n) || n < 0 || n > 255)) throw TypeError("Illegal str: Not a debug encoded string");
o.view[a++] = n;
l = !0;
}
if (w) throw TypeError("Illegal str: Invalid symbol at " + h);
}
if (!i) {
if (!u || !y) throw TypeError("Illegal str: Missing offset or limit");
if (a < o.buffer.byteLength) throw TypeError("Illegal str: Not a debug encoded string (is it hex?) " + a + " < " + s);
}
return o;
};
r.toHex = function(e, t) {
e = "undefined" == typeof e ? this.offset : e;
t = "undefined" == typeof t ? this.limit : t;
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal begin: Not an integer");
e >>>= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal end: Not an integer");
t >>>= 0;
if (e < 0 || e > t || t > this.buffer.byteLength) throw RangeError("Illegal range: 0 <= " + e + " <= " + t + " <= " + this.buffer.byteLength);
}
for (var r, i = new Array(t - e); e < t; ) (r = this.view[e++]) < 16 ? i.push("0", r.toString(16)) : i.push(r.toString(16));
return i.join("");
};
t.fromHex = function(e, r, i) {
if (!i) {
if ("string" != typeof e) throw TypeError("Illegal str: Not a string");
if (e.length % 2 != 0) throw TypeError("Illegal str: Length not a multiple of 2");
}
for (var f, n = e.length, s = new t(n / 2 | 0, r), o = 0, h = 0; o < n; o += 2) {
f = parseInt(e.substring(o, o + 2), 16);
if (!i && (!isFinite(f) || f < 0 || f > 255)) throw TypeError("Illegal str: Contains non-hex characters");
s.view[h++] = f;
}
s.limit = h;
return s;
};
var l = function() {
var e = {
MAX_CODEPOINT: 1114111,
encodeUTF8: function(e, t) {
var r = null;
"number" == typeof e && (r = e, e = function() {
return null;
});
for (;null !== r || null !== (r = e()); ) {
r < 128 ? t(127 & r) : r < 2048 ? (t(r >> 6 & 31 | 192), t(63 & r | 128)) : r < 65536 ? (t(r >> 12 & 15 | 224), 
t(r >> 6 & 63 | 128), t(63 & r | 128)) : (t(r >> 18 & 7 | 240), t(r >> 12 & 63 | 128), 
t(r >> 6 & 63 | 128), t(63 & r | 128));
r = null;
}
},
decodeUTF8: function(e, t) {
for (var r, i, f, n, s = function(e) {
e = e.slice(0, e.indexOf(null));
var t = Error(e.toString());
t.name = "TruncatedError";
t.bytes = e;
throw t;
}; null !== (r = e()); ) if (0 == (128 & r)) t(r); else if (192 == (224 & r)) null === (i = e()) && s([ r, i ]), 
t((31 & r) << 6 | 63 & i); else if (224 == (240 & r)) (null === (i = e()) || null === (f = e())) && s([ r, i, f ]), 
t((15 & r) << 12 | (63 & i) << 6 | 63 & f); else {
if (240 != (248 & r)) throw RangeError("Illegal starting byte: " + r);
(null === (i = e()) || null === (f = e()) || null === (n = e())) && s([ r, i, f, n ]), 
t((7 & r) << 18 | (63 & i) << 12 | (63 & f) << 6 | 63 & n);
}
},
UTF16toUTF8: function(e, t) {
for (var r, i = null; null !== (r = null !== i ? i : e()); ) if (r >= 55296 && r <= 57343 && null !== (i = e()) && i >= 56320 && i <= 57343) {
t(1024 * (r - 55296) + i - 56320 + 65536);
i = null;
} else t(r);
null !== i && t(i);
},
UTF8toUTF16: function(e, t) {
var r = null;
"number" == typeof e && (r = e, e = function() {
return null;
});
for (;null !== r || null !== (r = e()); ) {
r <= 65535 ? t(r) : (t(55296 + ((r -= 65536) >> 10)), t(r % 1024 + 56320));
r = null;
}
},
encodeUTF16toUTF8: function(t, r) {
e.UTF16toUTF8(t, function(t) {
e.encodeUTF8(t, r);
});
},
decodeUTF8toUTF16: function(t, r) {
e.decodeUTF8(t, function(t) {
e.UTF8toUTF16(t, r);
});
},
calculateCodePoint: function(e) {
return e < 128 ? 1 : e < 2048 ? 2 : e < 65536 ? 3 : 4;
},
calculateUTF8: function(e) {
for (var t, r = 0; null !== (t = e()); ) r += t < 128 ? 1 : t < 2048 ? 2 : t < 65536 ? 3 : 4;
return r;
},
calculateUTF16asUTF8: function(t) {
var r = 0, i = 0;
e.UTF16toUTF8(t, function(e) {
++r;
i += e < 128 ? 1 : e < 2048 ? 2 : e < 65536 ? 3 : 4;
});
return [ r, i ];
}
};
return e;
}();
r.toUTF8 = function(e, t) {
"undefined" == typeof e && (e = this.offset);
"undefined" == typeof t && (t = this.limit);
if (!this.noAssert) {
if ("number" != typeof e || e % 1 != 0) throw TypeError("Illegal begin: Not an integer");
e >>>= 0;
if ("number" != typeof t || t % 1 != 0) throw TypeError("Illegal end: Not an integer");
t >>>= 0;
if (e < 0 || e > t || t > this.buffer.byteLength) throw RangeError("Illegal range: 0 <= " + e + " <= " + t + " <= " + this.buffer.byteLength);
}
var r;
try {
l.decodeUTF8toUTF16(function() {
return e < t ? this.view[e++] : null;
}.bind(this), r = s());
} catch (r) {
if (e !== t) throw RangeError("Illegal range: Truncated data, " + e + " != " + t);
}
return r();
};
t.fromUTF8 = function(e, r, i) {
if (!i && "string" != typeof e) throw TypeError("Illegal str: Not a string");
var f = new t(l.calculateUTF16asUTF8(n(e), !0)[1], r, i), s = 0;
l.encodeUTF16toUTF8(n(e), function(e) {
f.view[s++] = e;
});
f.limit = s;
return f;
};
return t;
});
})();