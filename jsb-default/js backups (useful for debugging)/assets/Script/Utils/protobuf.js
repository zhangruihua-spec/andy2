(function() {
(function(e, t) {
"function" == typeof define && define.amd ? define([ "assets/Script/Utils/bytebuffer" ], t) : "function" == typeof require && "object" == typeof module && module && module.exports || ((e.dcodeIO = e.dcodeIO || {}).ProtoBuf = t(e.dcodeIO.ByteBuffer));
})(this, function(e, t) {
"use strict";
var i = window.ProtoBuf = {};
i.ByteBuffer = e;
i.Long = e.Long || null;
i.VERSION = "5.0.1";
i.WIRE_TYPES = {};
i.WIRE_TYPES.VARINT = 0;
i.WIRE_TYPES.BITS64 = 1;
i.WIRE_TYPES.LDELIM = 2;
i.WIRE_TYPES.STARTGROUP = 3;
i.WIRE_TYPES.ENDGROUP = 4;
i.WIRE_TYPES.BITS32 = 5;
i.PACKABLE_WIRE_TYPES = [ i.WIRE_TYPES.VARINT, i.WIRE_TYPES.BITS64, i.WIRE_TYPES.BITS32 ];
i.TYPES = {
int32: {
name: "int32",
wireType: i.WIRE_TYPES.VARINT,
defaultValue: 0
},
uint32: {
name: "uint32",
wireType: i.WIRE_TYPES.VARINT,
defaultValue: 0
},
sint32: {
name: "sint32",
wireType: i.WIRE_TYPES.VARINT,
defaultValue: 0
},
int64: {
name: "int64",
wireType: i.WIRE_TYPES.VARINT,
defaultValue: i.Long ? i.Long.ZERO : void 0
},
uint64: {
name: "uint64",
wireType: i.WIRE_TYPES.VARINT,
defaultValue: i.Long ? i.Long.UZERO : void 0
},
sint64: {
name: "sint64",
wireType: i.WIRE_TYPES.VARINT,
defaultValue: i.Long ? i.Long.ZERO : void 0
},
bool: {
name: "bool",
wireType: i.WIRE_TYPES.VARINT,
defaultValue: !1
},
double: {
name: "double",
wireType: i.WIRE_TYPES.BITS64,
defaultValue: 0
},
string: {
name: "string",
wireType: i.WIRE_TYPES.LDELIM,
defaultValue: ""
},
bytes: {
name: "bytes",
wireType: i.WIRE_TYPES.LDELIM,
defaultValue: null
},
fixed32: {
name: "fixed32",
wireType: i.WIRE_TYPES.BITS32,
defaultValue: 0
},
sfixed32: {
name: "sfixed32",
wireType: i.WIRE_TYPES.BITS32,
defaultValue: 0
},
fixed64: {
name: "fixed64",
wireType: i.WIRE_TYPES.BITS64,
defaultValue: i.Long ? i.Long.UZERO : void 0
},
sfixed64: {
name: "sfixed64",
wireType: i.WIRE_TYPES.BITS64,
defaultValue: i.Long ? i.Long.ZERO : void 0
},
float: {
name: "float",
wireType: i.WIRE_TYPES.BITS32,
defaultValue: 0
},
enum: {
name: "enum",
wireType: i.WIRE_TYPES.VARINT,
defaultValue: 0
},
message: {
name: "message",
wireType: i.WIRE_TYPES.LDELIM,
defaultValue: null
},
group: {
name: "group",
wireType: i.WIRE_TYPES.STARTGROUP,
defaultValue: null
}
};
i.MAP_KEY_TYPES = [ i.TYPES.int32, i.TYPES.sint32, i.TYPES.sfixed32, i.TYPES.uint32, i.TYPES.fixed32, i.TYPES.int64, i.TYPES.sint64, i.TYPES.sfixed64, i.TYPES.uint64, i.TYPES.fixed64, i.TYPES.bool, i.TYPES.string, i.TYPES.bytes ];
i.ID_MIN = 1;
i.ID_MAX = 536870911;
i.convertFieldsToCamelCase = !1;
i.populateAccessors = !0;
i.populateDefaults = !0;
i.Util = function() {
var e = {};
e.IS_NODE = !("object" != typeof process || process + "" != "[object process]" || process.browser);
e.XHR = function() {
for (var e = [ function() {
return new XMLHttpRequest();
}, function() {
return new ActiveXObject("Msxml2.XMLHTTP");
}, function() {
return new ActiveXObject("Msxml3.XMLHTTP");
}, function() {
return new ActiveXObject("Microsoft.XMLHTTP");
} ], t = null, i = 0; i < e.length; i++) {
try {
t = e[i]();
} catch (e) {
continue;
}
break;
}
if (!t) throw Error("XMLHttpRequest is not supported");
return t;
};
e.fetch = function(t, i) {
i && "function" != typeof i && (i = null);
if (e.IS_NODE) {
var r = require("fs");
if (i) r.readFile(t, function(e, t) {
i(e ? null : "" + t);
}); else try {
return r.readFileSync(t);
} catch (e) {
return null;
}
} else {
var n = e.XHR();
n.open("GET", t, !!i);
n.setRequestHeader("Accept", "text/plain");
"function" == typeof n.overrideMimeType && n.overrideMimeType("text/plain");
if (!i) {
n.send(null);
return 200 == n.status || 0 == n.status && "string" == typeof n.responseText ? n.responseText : null;
}
n.onreadystatechange = function() {
4 == n.readyState && (200 == n.status || 0 == n.status && "string" == typeof n.responseText ? i(n.responseText) : i(null));
};
if (4 == n.readyState) return;
n.send(null);
}
};
e.toCamelCase = function(e) {
return e.replace(/_([a-zA-Z])/g, function(e, t) {
return t.toUpperCase();
});
};
return e;
}();
i.Lang = {
DELIM: /[\s\{\}=;:\[\],'"\(\)<>]/g,
RULE: /^(?:required|optional|repeated|map)$/,
TYPE: /^(?:double|float|int32|uint32|sint32|int64|uint64|sint64|fixed32|sfixed32|fixed64|sfixed64|bool|string|bytes)$/,
NAME: /^[a-zA-Z_][a-zA-Z_0-9]*$/,
TYPEDEF: /^[a-zA-Z][a-zA-Z_0-9]*$/,
TYPEREF: /^(?:\.?[a-zA-Z_][a-zA-Z_0-9]*)+$/,
FQTYPEREF: /^(?:\.[a-zA-Z][a-zA-Z_0-9]*)+$/,
NUMBER: /^-?(?:[1-9][0-9]*|0|0[xX][0-9a-fA-F]+|0[0-7]+|([0-9]*(\.[0-9]*)?([Ee][+-]?[0-9]+)?)|inf|nan)$/,
NUMBER_DEC: /^(?:[1-9][0-9]*|0)$/,
NUMBER_HEX: /^0[xX][0-9a-fA-F]+$/,
NUMBER_OCT: /^0[0-7]+$/,
NUMBER_FLT: /^([0-9]*(\.[0-9]*)?([Ee][+-]?[0-9]+)?|inf|nan)$/,
BOOL: /^(?:true|false)$/i,
ID: /^(?:[1-9][0-9]*|0|0[xX][0-9a-fA-F]+|0[0-7]+)$/,
NEGID: /^\-?(?:[1-9][0-9]*|0|0[xX][0-9a-fA-F]+|0[0-7]+)$/,
WHITESPACE: /\s/,
STRING: /(?:"([^"\\]*(?:\\.[^"\\]*)*)")|(?:'([^'\\]*(?:\\.[^'\\]*)*)')/g,
STRING_DQ: /(?:"([^"\\]*(?:\\.[^"\\]*)*)")/g,
STRING_SQ: /(?:'([^'\\]*(?:\\.[^'\\]*)*)')/g
};
i.DotProto = function(e, t) {
var i = {}, r = function(e) {
this.source = e + "";
this.index = 0;
this.line = 1;
this.stack = [];
this._stringOpen = null;
}, n = r.prototype;
n._readString = function() {
var e = '"' === this._stringOpen ? t.STRING_DQ : t.STRING_SQ;
e.lastIndex = this.index - 1;
var i = e.exec(this.source);
if (!i) throw Error("unterminated string");
this.index = e.lastIndex;
this.stack.push(this._stringOpen);
this._stringOpen = null;
return i[1];
};
n.next = function() {
if (this.stack.length > 0) return this.stack.shift();
if (this.index >= this.source.length) return null;
if (null !== this._stringOpen) return this._readString();
var e, i, r;
do {
e = !1;
for (;t.WHITESPACE.test(r = this.source.charAt(this.index)); ) {
"\n" === r && ++this.line;
if (++this.index === this.source.length) return null;
}
if ("/" === this.source.charAt(this.index)) {
++this.index;
if ("/" === this.source.charAt(this.index)) {
for (;"\n" !== this.source.charAt(++this.index); ) if (this.index == this.source.length) return null;
++this.index;
++this.line;
e = !0;
} else {
if ("*" !== (r = this.source.charAt(this.index))) return "/";
do {
"\n" === r && ++this.line;
if (++this.index === this.source.length) return null;
i = r;
r = this.source.charAt(this.index);
} while ("*" !== i || "/" !== r);
++this.index;
e = !0;
}
}
} while (e);
if (this.index === this.source.length) return null;
var n = this.index;
t.DELIM.lastIndex = 0;
if (!t.DELIM.test(this.source.charAt(n++))) for (;n < this.source.length && !t.DELIM.test(this.source.charAt(n)); ) ++n;
var s = this.source.substring(this.index, this.index = n);
'"' !== s && "'" !== s || (this._stringOpen = s);
return s;
};
n.peek = function() {
if (0 === this.stack.length) {
var e = this.next();
if (null === e) return null;
this.stack.push(e);
}
return this.stack[0];
};
n.skip = function(e) {
var t = this.next();
if (t !== e) throw Error("illegal '" + t + "', '" + e + "' expected");
};
n.omit = function(e) {
if (this.peek() === e) {
this.next();
return !0;
}
return !1;
};
n.toString = function() {
return "Tokenizer (" + this.index + "/" + this.source.length + " at line " + this.line + ")";
};
i.Tokenizer = r;
var s = function(e) {
this.tn = new r(e);
this.proto3 = !1;
}, o = s.prototype;
o.parse = function() {
var e, i, r = {
name: "[ROOT]",
package: null,
messages: [],
enums: [],
imports: [],
options: {},
services: []
}, n = !0;
try {
for (;e = this.tn.next(); ) switch (e) {
case "package":
if (!n || null !== r.package) throw Error("unexpected 'package'");
e = this.tn.next();
if (!t.TYPEREF.test(e)) throw Error("illegal package name: " + e);
this.tn.skip(";");
r.package = e;
break;

case "import":
if (!n) throw Error("unexpected 'import'");
("public" === (e = this.tn.peek()) || (i = "weak" === e)) && this.tn.next();
e = this._readString();
this.tn.skip(";");
i || r.imports.push(e);
break;

case "syntax":
if (!n) throw Error("unexpected 'syntax'");
this.tn.skip("=");
"proto3" === (r.syntax = this._readString()) && (this.proto3 = !0);
this.tn.skip(";");
break;

case "message":
this._parseMessage(r, null);
n = !1;
break;

case "enum":
this._parseEnum(r);
n = !1;
break;

case "option":
this._parseOption(r);
break;

case "service":
this._parseService(r);
break;

case "extend":
this._parseExtend(r);
break;

default:
throw Error("unexpected '" + e + "'");
}
} catch (e) {
e.message = "Parse error at line " + this.tn.line + ": " + e.message;
throw e;
}
delete r.name;
return r;
};
s.parse = function(e) {
return new s(e).parse();
};
function a(e, i) {
var r = -1, n = 1;
if ("-" == e.charAt(0)) {
n = -1;
e = e.substring(1);
}
if (t.NUMBER_DEC.test(e)) r = parseInt(e); else if (t.NUMBER_HEX.test(e)) r = parseInt(e.substring(2), 16); else {
if (!t.NUMBER_OCT.test(e)) throw Error("illegal id value: " + (n < 0 ? "-" : "") + e);
r = parseInt(e.substring(1), 8);
}
r = n * r | 0;
if (!i && r < 0) throw Error("illegal id value: " + (n < 0 ? "-" : "") + e);
return r;
}
function l(e) {
var i = 1;
if ("-" == e.charAt(0)) {
i = -1;
e = e.substring(1);
}
if (t.NUMBER_DEC.test(e)) return i * parseInt(e, 10);
if (t.NUMBER_HEX.test(e)) return i * parseInt(e.substring(2), 16);
if (t.NUMBER_OCT.test(e)) return i * parseInt(e.substring(1), 8);
if ("inf" === e) return Infinity * i;
if ("nan" === e) return NaN;
if (t.NUMBER_FLT.test(e)) return i * parseFloat(e);
throw Error("illegal number value: " + (i < 0 ? "-" : "") + e);
}
o._readString = function() {
var e, t, i = "";
do {
if ("'" !== (t = this.tn.next()) && '"' !== t) throw Error("illegal string delimiter: " + t);
i += this.tn.next();
this.tn.skip(t);
e = this.tn.peek();
} while ('"' === e || '"' === e);
return i;
};
o._readValue = function(e) {
var i = this.tn.peek();
if ('"' === i || "'" === i) return this._readString();
this.tn.next();
if (t.NUMBER.test(i)) return l(i);
if (t.BOOL.test(i)) return "true" === i.toLowerCase();
if (e && t.TYPEREF.test(i)) return i;
throw Error("illegal value: " + i);
};
o._parseOption = function(e, i) {
var r = this.tn.next(), n = !1;
if ("(" === r) {
n = !0;
r = this.tn.next();
}
if (!t.TYPEREF.test(r)) throw Error("illegal option name: " + r);
var s = r;
if (n) {
this.tn.skip(")");
s = "(" + s + ")";
r = this.tn.peek();
if (t.FQTYPEREF.test(r)) {
s += r;
this.tn.next();
}
}
this.tn.skip("=");
this._parseOptionValue(e, s);
i || this.tn.skip(";");
};
function f(e, t, i) {
if ("undefined" == typeof e[t]) e[t] = i; else {
Array.isArray(e[t]) || (e[t] = [ e[t] ]);
e[t].push(i);
}
}
o._parseOptionValue = function(e, i) {
var r = this.tn.peek();
if ("{" !== r) f(e.options, i, this._readValue(!0)); else {
this.tn.skip("{");
for (;"}" !== (r = this.tn.next()); ) {
if (!t.NAME.test(r)) throw Error("illegal option name: " + i + "." + r);
this.tn.omit(":") ? f(e.options, i + "." + r, this._readValue(!0)) : this._parseOptionValue(e, i + "." + r);
}
}
};
o._parseService = function(e) {
var i = this.tn.next();
if (!t.NAME.test(i)) throw Error("illegal service name at line " + this.tn.line + ": " + i);
var r = {
name: i,
rpc: {},
options: {}
};
this.tn.skip("{");
for (;"}" !== (i = this.tn.next()); ) if ("option" === i) this._parseOption(r); else {
if ("rpc" !== i) throw Error("illegal service token: " + i);
this._parseServiceRPC(r);
}
this.tn.omit(";");
e.services.push(r);
};
o._parseServiceRPC = function(e) {
var i = this.tn.next();
if (!t.NAME.test(i)) throw Error("illegal rpc service method name: " + i);
var r = i, n = {
request: null,
response: null,
request_stream: !1,
response_stream: !1,
options: {}
};
this.tn.skip("(");
if ("stream" === (i = this.tn.next()).toLowerCase()) {
n.request_stream = !0;
i = this.tn.next();
}
if (!t.TYPEREF.test(i)) throw Error("illegal rpc service request type: " + i);
n.request = i;
this.tn.skip(")");
if ("returns" !== (i = this.tn.next()).toLowerCase()) throw Error("illegal rpc service request type delimiter: " + i);
this.tn.skip("(");
if ("stream" === (i = this.tn.next()).toLowerCase()) {
n.response_stream = !0;
i = this.tn.next();
}
n.response = i;
this.tn.skip(")");
if ("{" === (i = this.tn.peek())) {
this.tn.next();
for (;"}" !== (i = this.tn.next()); ) {
if ("option" !== i) throw Error("illegal rpc service token: " + i);
this._parseOption(n);
}
this.tn.omit(";");
} else this.tn.skip(";");
"undefined" == typeof e.rpc && (e.rpc = {});
e.rpc[r] = n;
};
o._parseMessage = function(e, i) {
var r = !!i, n = this.tn.next(), s = {
name: "",
fields: [],
enums: [],
messages: [],
options: {},
services: [],
oneofs: {}
};
if (!t.NAME.test(n)) throw Error("illegal " + (r ? "group" : "message") + " name: " + n);
s.name = n;
if (r) {
this.tn.skip("=");
i.id = a(this.tn.next());
s.isGroup = !0;
}
"[" === (n = this.tn.peek()) && i && this._parseFieldOptions(i);
this.tn.skip("{");
for (;"}" !== (n = this.tn.next()); ) if (t.RULE.test(n)) this._parseMessageField(s, n); else if ("oneof" === n) this._parseMessageOneOf(s); else if ("enum" === n) this._parseEnum(s); else if ("message" === n) this._parseMessage(s); else if ("option" === n) this._parseOption(s); else if ("service" === n) this._parseService(s); else if ("extensions" === n) s.hasOwnProperty("extensions") ? s.extensions = s.extensions.concat(this._parseExtensionRanges()) : s.extensions = this._parseExtensionRanges(); else if ("reserved" === n) this._parseIgnored(); else if ("extend" === n) this._parseExtend(s); else {
if (!t.TYPEREF.test(n)) throw Error("illegal message token: " + n);
if (!this.proto3) throw Error("illegal field rule: " + n);
this._parseMessageField(s, "optional", n);
}
this.tn.omit(";");
e.messages.push(s);
return s;
};
o._parseIgnored = function() {
for (;";" !== this.tn.peek(); ) this.tn.next();
this.tn.skip(";");
};
o._parseMessageField = function(e, i, r) {
if (!t.RULE.test(i)) throw Error("illegal message field rule: " + i);
var n, s = {
rule: i,
type: "",
name: "",
options: {},
id: 0
};
if ("map" === i) {
if (r) throw Error("illegal type: " + r);
this.tn.skip("<");
n = this.tn.next();
if (!t.TYPE.test(n) && !t.TYPEREF.test(n)) throw Error("illegal message field type: " + n);
s.keytype = n;
this.tn.skip(",");
n = this.tn.next();
if (!t.TYPE.test(n) && !t.TYPEREF.test(n)) throw Error("illegal message field: " + n);
s.type = n;
this.tn.skip(">");
n = this.tn.next();
if (!t.NAME.test(n)) throw Error("illegal message field name: " + n);
s.name = n;
this.tn.skip("=");
s.id = a(this.tn.next());
"[" === (n = this.tn.peek()) && this._parseFieldOptions(s);
this.tn.skip(";");
} else if ("group" === (r = "undefined" != typeof r ? r : this.tn.next())) {
var o = this._parseMessage(e, s);
if (!/^[A-Z]/.test(o.name)) throw Error("illegal group name: " + o.name);
s.type = o.name;
s.name = o.name.toLowerCase();
this.tn.omit(";");
} else {
if (!t.TYPE.test(r) && !t.TYPEREF.test(r)) throw Error("illegal message field type: " + r);
s.type = r;
n = this.tn.next();
if (!t.NAME.test(n)) throw Error("illegal message field name: " + n);
s.name = n;
this.tn.skip("=");
s.id = a(this.tn.next());
"[" === (n = this.tn.peek()) && this._parseFieldOptions(s);
this.tn.skip(";");
}
e.fields.push(s);
return s;
};
o._parseMessageOneOf = function(e) {
var i = this.tn.next();
if (!t.NAME.test(i)) throw Error("illegal oneof name: " + i);
var r, n = i, s = [];
this.tn.skip("{");
for (;"}" !== (i = this.tn.next()); ) {
(r = this._parseMessageField(e, "optional", i)).oneof = n;
s.push(r.id);
}
this.tn.omit(";");
e.oneofs[n] = s;
};
o._parseFieldOptions = function(e) {
this.tn.skip("[");
for (var t = !0; "]" !== this.tn.peek(); ) {
t || this.tn.skip(",");
this._parseOption(e, !0);
t = !1;
}
this.tn.next();
};
o._parseEnum = function(e) {
var i = {
name: "",
values: [],
options: {}
}, r = this.tn.next();
if (!t.NAME.test(r)) throw Error("illegal name: " + r);
i.name = r;
this.tn.skip("{");
for (;"}" !== (r = this.tn.next()); ) if ("option" === r) this._parseOption(i); else {
if (!t.NAME.test(r)) throw Error("illegal name: " + r);
this.tn.skip("=");
var n = {
name: r,
id: a(this.tn.next(), !0)
};
"[" === (r = this.tn.peek()) && this._parseFieldOptions({
options: {}
});
this.tn.skip(";");
i.values.push(n);
}
this.tn.omit(";");
e.enums.push(i);
};
o._parseExtensionRanges = function() {
var t, i, r, n = [];
do {
i = [];
for (;;) {
switch (t = this.tn.next()) {
case "min":
r = e.ID_MIN;
break;

case "max":
r = e.ID_MAX;
break;

default:
r = l(t);
}
i.push(r);
if (2 === i.length) break;
if ("to" !== this.tn.peek()) {
i.push(r);
break;
}
this.tn.next();
}
n.push(i);
} while (this.tn.omit(","));
this.tn.skip(";");
return n;
};
o._parseExtend = function(e) {
var i = this.tn.next();
if (!t.TYPEREF.test(i)) throw Error("illegal extend reference: " + i);
var r = {
ref: i,
fields: []
};
this.tn.skip("{");
for (;"}" !== (i = this.tn.next()); ) if (t.RULE.test(i)) this._parseMessageField(r, i); else {
if (!t.TYPEREF.test(i)) throw Error("illegal extend token: " + i);
if (!this.proto3) throw Error("illegal field rule: " + i);
this._parseMessageField(r, "optional", i);
}
this.tn.omit(";");
e.messages.push(r);
return r;
};
o.toString = function() {
return "Parser at line " + this.tn.line;
};
i.Parser = s;
return i;
}(i, i.Lang);
i.Reflect = function(t) {
var i = {}, r = function(e, t, i) {
this.builder = e;
this.parent = t;
this.name = i;
this.className;
}, n = r.prototype;
n.fqn = function() {
for (var e = this.name, t = this; ;) {
if (null == (t = t.parent)) break;
e = t.name + "." + e;
}
return e;
};
n.toString = function(e) {
return (e ? this.className + " " : "") + this.fqn();
};
n.build = function() {
throw Error(this.toString(!0) + " cannot be built directly");
};
i.T = r;
var s = function(e, t, i, n, s) {
r.call(this, e, t, i);
this.className = "Namespace";
this.children = [];
this.options = n || {};
this.syntax = s || "proto2";
}, o = s.prototype = Object.create(r.prototype);
o.getChildren = function(e) {
if (null == (e = e || null)) return this.children.slice();
for (var t = [], i = 0, r = this.children.length; i < r; ++i) this.children[i] instanceof e && t.push(this.children[i]);
return t;
};
o.addChild = function(e) {
var t;
if (t = this.getChild(e.name)) if (t instanceof u.Field && t.name !== t.originalName && null === this.getChild(t.originalName)) t.name = t.originalName; else {
if (!(e instanceof u.Field && e.name !== e.originalName && null === this.getChild(e.originalName))) throw Error("Duplicate name in namespace " + this.toString(!0) + ": " + e.name);
e.name = e.originalName;
}
this.children.push(e);
};
o.getChild = function(e) {
for (var t = "number" == typeof e ? "id" : "name", i = 0, r = this.children.length; i < r; ++i) if (this.children[i][t] === e) return this.children[i];
return null;
};
o.resolve = function(e, t) {
var r, n = "string" == typeof e ? e.split(".") : e, s = this, o = 0;
if ("" === n[o]) {
for (;null !== s.parent; ) s = s.parent;
o++;
}
do {
do {
if (!(s instanceof i.Namespace)) {
s = null;
break;
}
if (!(r = s.getChild(n[o])) || !(r instanceof i.T) || t && !(r instanceof i.Namespace)) {
s = null;
break;
}
s = r;
o++;
} while (o < n.length);
if (null != s) break;
if (null !== this.parent) return this.parent.resolve(e, t);
} while (null != s);
return s;
};
o.qn = function(e) {
var t = [], r = e;
do {
t.unshift(r.name);
r = r.parent;
} while (null !== r);
for (var n = 1; n <= t.length; n++) {
var s = t.slice(t.length - n);
if (e === this.resolve(s, e instanceof i.Namespace)) return s.join(".");
}
return e.fqn();
};
o.build = function() {
for (var e, t = {}, i = this.children, r = 0, n = i.length; r < n; ++r) (e = i[r]) instanceof s && (t[e.name] = e.build());
Object.defineProperty && Object.defineProperty(t, "$options", {
value: this.buildOpt()
});
return t;
};
o.buildOpt = function() {
for (var e = {}, t = Object.keys(this.options), i = 0, r = t.length; i < r; ++i) {
var n = t[i], s = this.options[t[i]];
e[n] = s;
}
return e;
};
o.getOption = function(e) {
return "undefined" == typeof e ? this.options : "undefined" != typeof this.options[e] ? this.options[e] : null;
};
i.Namespace = s;
var a = function(e, i, r, n, s) {
this.type = e;
this.resolvedType = i;
this.isMapKey = r;
this.syntax = n;
this.name = s;
if (r && t.MAP_KEY_TYPES.indexOf(e) < 0) throw Error("Invalid map key type: " + e.name);
}, l = a.prototype;
a.defaultFieldValue = function(i) {
"string" == typeof i && (i = t.TYPES[i]);
if ("undefined" == typeof i.defaultValue) throw Error("default value for type " + i.name + " is not supported");
return i == t.TYPES.bytes ? new e(0) : i.defaultValue;
};
function f(e, i) {
if (e && "number" == typeof e.low && "number" == typeof e.high && "boolean" == typeof e.unsigned && e.low == e.low && e.high == e.high) return new t.Long(e.low, e.high, "undefined" == typeof i ? e.unsigned : i);
if ("string" == typeof e) return t.Long.fromString(e, i || !1, 10);
if ("number" == typeof e) return t.Long.fromNumber(e, i || !1);
throw Error("not convertible to Long");
}
l.toString = function() {
return (this.name || "") + (this.isMapKey ? "map" : "value") + " element";
};
l.verifyValue = function(i) {
var r = this;
function n(e, t) {
throw Error("Illegal value for " + r.toString(!0) + " of type " + r.type.name + ": " + e + " (" + t + ")");
}
switch (this.type) {
case t.TYPES.int32:
case t.TYPES.sint32:
case t.TYPES.sfixed32:
("number" != typeof i || i == i && i % 1 != 0) && n(typeof i, "not an integer");
return i > 4294967295 ? 0 | i : i;

case t.TYPES.uint32:
case t.TYPES.fixed32:
("number" != typeof i || i == i && i % 1 != 0) && n(typeof i, "not an integer");
return i < 0 ? i >>> 0 : i;

case t.TYPES.int64:
case t.TYPES.sint64:
case t.TYPES.sfixed64:
if (t.Long) try {
return f(i, !1);
} catch (e) {
n(typeof i, e.message);
} else n(typeof i, "requires Long.js");

case t.TYPES.uint64:
case t.TYPES.fixed64:
if (t.Long) try {
return f(i, !0);
} catch (e) {
n(typeof i, e.message);
} else n(typeof i, "requires Long.js");

case t.TYPES.bool:
"boolean" != typeof i && n(typeof i, "not a boolean");
return i;

case t.TYPES.float:
case t.TYPES.double:
"number" != typeof i && n(typeof i, "not a number");
return i;

case t.TYPES.string:
"string" == typeof i || i && i instanceof String || n(typeof i, "not a string");
return "" + i;

case t.TYPES.bytes:
return e.isByteBuffer(i) ? i : e.wrap(i, "base64");

case t.TYPES.enum:
var s = this.resolvedType.getChildren(t.Reflect.Enum.Value);
for (a = 0; a < s.length; a++) {
if (s[a].name == i) return s[a].id;
if (s[a].id == i) return s[a].id;
}
if ("proto3" === this.syntax) {
("number" != typeof i || i == i && i % 1 != 0) && n(typeof i, "not an integer");
(i > 4294967295 || i < 0) && n(typeof i, "not in range for uint32");
return i;
}
n(i, "not a valid enum value");

case t.TYPES.group:
case t.TYPES.message:
i && "object" == typeof i || n(typeof i, "object expected");
if (i instanceof this.resolvedType.clazz) return i;
if (i instanceof t.Builder.Message) {
var o = {};
for (var a in i) i.hasOwnProperty(a) && (o[a] = i[a]);
i = o;
}
return new this.resolvedType.clazz(i);
}
throw Error("[INTERNAL] Illegal value for " + this.toString(!0) + ": " + i + " (undefined type " + this.type + ")");
};
l.calculateLength = function(i, r) {
if (null === r) return 0;
var n;
switch (this.type) {
case t.TYPES.int32:
return r < 0 ? e.calculateVarint64(r) : e.calculateVarint32(r);

case t.TYPES.uint32:
return e.calculateVarint32(r);

case t.TYPES.sint32:
return e.calculateVarint32(e.zigZagEncode32(r));

case t.TYPES.fixed32:
case t.TYPES.sfixed32:
case t.TYPES.float:
return 4;

case t.TYPES.int64:
case t.TYPES.uint64:
return e.calculateVarint64(r);

case t.TYPES.sint64:
return e.calculateVarint64(e.zigZagEncode64(r));

case t.TYPES.fixed64:
case t.TYPES.sfixed64:
return 8;

case t.TYPES.bool:
return 1;

case t.TYPES.enum:
return e.calculateVarint32(r);

case t.TYPES.double:
return 8;

case t.TYPES.string:
n = e.calculateUTF8Bytes(r);
return e.calculateVarint32(n) + n;

case t.TYPES.bytes:
if (r.remaining() < 0) throw Error("Illegal value for " + this.toString(!0) + ": " + r.remaining() + " bytes remaining");
return e.calculateVarint32(r.remaining()) + r.remaining();

case t.TYPES.message:
n = this.resolvedType.calculate(r);
return e.calculateVarint32(n) + n;

case t.TYPES.group:
return (n = this.resolvedType.calculate(r)) + e.calculateVarint32(i << 3 | t.WIRE_TYPES.ENDGROUP);
}
throw Error("[INTERNAL] Illegal value to encode in " + this.toString(!0) + ": " + r + " (unknown type)");
};
l.encodeValue = function(i, r, n) {
if (null === r) return n;
switch (this.type) {
case t.TYPES.int32:
r < 0 ? n.writeVarint64(r) : n.writeVarint32(r);
break;

case t.TYPES.uint32:
n.writeVarint32(r);
break;

case t.TYPES.sint32:
n.writeVarint32ZigZag(r);
break;

case t.TYPES.fixed32:
n.writeUint32(r);
break;

case t.TYPES.sfixed32:
n.writeInt32(r);
break;

case t.TYPES.int64:
case t.TYPES.uint64:
n.writeVarint64(r);
break;

case t.TYPES.sint64:
n.writeVarint64ZigZag(r);
break;

case t.TYPES.fixed64:
n.writeUint64(r);
break;

case t.TYPES.sfixed64:
n.writeInt64(r);
break;

case t.TYPES.bool:
"string" == typeof r ? n.writeVarint32("false" === r.toLowerCase() ? 0 : !!r) : n.writeVarint32(r ? 1 : 0);
break;

case t.TYPES.enum:
n.writeVarint32(r);
break;

case t.TYPES.float:
n.writeFloat32(r);
break;

case t.TYPES.double:
n.writeFloat64(r);
break;

case t.TYPES.string:
n.writeVString(r);
break;

case t.TYPES.bytes:
if (r.remaining() < 0) throw Error("Illegal value for " + this.toString(!0) + ": " + r.remaining() + " bytes remaining");
var s = r.offset;
n.writeVarint32(r.remaining());
n.append(r);
r.offset = s;
break;

case t.TYPES.message:
var o = new e().LE();
this.resolvedType.encode(r, o);
n.writeVarint32(o.offset);
n.append(o.flip());
break;

case t.TYPES.group:
this.resolvedType.encode(r, n);
n.writeVarint32(i << 3 | t.WIRE_TYPES.ENDGROUP);
break;

default:
throw Error("[INTERNAL] Illegal value to encode in " + this.toString(!0) + ": " + r + " (unknown type)");
}
return n;
};
l.decode = function(e, i, r) {
if (i != this.type.wireType) throw Error("Unexpected wire type for element");
var n, s;
switch (this.type) {
case t.TYPES.int32:
return 0 | e.readVarint32();

case t.TYPES.uint32:
return e.readVarint32() >>> 0;

case t.TYPES.sint32:
return 0 | e.readVarint32ZigZag();

case t.TYPES.fixed32:
return e.readUint32() >>> 0;

case t.TYPES.sfixed32:
return 0 | e.readInt32();

case t.TYPES.int64:
return e.readVarint64();

case t.TYPES.uint64:
return e.readVarint64().toUnsigned();

case t.TYPES.sint64:
return e.readVarint64ZigZag();

case t.TYPES.fixed64:
return e.readUint64();

case t.TYPES.sfixed64:
return e.readInt64();

case t.TYPES.bool:
return !!e.readVarint32();

case t.TYPES.enum:
return e.readVarint32();

case t.TYPES.float:
return e.readFloat();

case t.TYPES.double:
return e.readDouble();

case t.TYPES.string:
return e.readVString();

case t.TYPES.bytes:
s = e.readVarint32();
if (e.remaining() < s) throw Error("Illegal number of bytes for " + this.toString(!0) + ": " + s + " required but got only " + e.remaining());
(n = e.clone()).limit = n.offset + s;
e.offset += s;
return n;

case t.TYPES.message:
s = e.readVarint32();
return this.resolvedType.decode(e, s);

case t.TYPES.group:
return this.resolvedType.decode(e, -1, r);
}
throw Error("[INTERNAL] Illegal decode type");
};
l.valueFromString = function(i) {
if (!this.isMapKey) throw Error("valueFromString() called on non-map-key element");
switch (this.type) {
case t.TYPES.int32:
case t.TYPES.sint32:
case t.TYPES.sfixed32:
case t.TYPES.uint32:
case t.TYPES.fixed32:
return this.verifyValue(parseInt(i));

case t.TYPES.int64:
case t.TYPES.sint64:
case t.TYPES.sfixed64:
case t.TYPES.uint64:
case t.TYPES.fixed64:
return this.verifyValue(i);

case t.TYPES.bool:
return "true" === i;

case t.TYPES.string:
return this.verifyValue(i);

case t.TYPES.bytes:
return e.fromBinary(i);
}
};
l.valueToString = function(e) {
if (!this.isMapKey) throw Error("valueToString() called on non-map-key element");
return this.type === t.TYPES.bytes ? e.toString("binary") : e.toString();
};
i.Element = a;
var u = function(e, t, i, r, n, o) {
s.call(this, e, t, i, r, o);
this.className = "Message";
this.extensions = void 0;
this.clazz = null;
this.isGroup = !!n;
this._fields = null;
this._fieldsById = null;
this._fieldsByName = null;
}, h = u.prototype = Object.create(s.prototype);
h.build = function(i) {
if (this.clazz && !i) return this.clazz;
var r = function(t, i) {
var r = i.getChildren(t.Reflect.Message.Field), n = i.getChildren(t.Reflect.Message.OneOf), s = function(o, a) {
t.Builder.Message.call(this);
for (var l = 0, f = n.length; l < f; ++l) this[n[l].name] = null;
for (l = 0, f = r.length; l < f; ++l) {
var u = r[l];
this[u.name] = u.repeated ? [] : u.map ? new t.Map(u) : null;
!u.required && "proto3" !== i.syntax || null === u.defaultValue || (this[u.name] = u.defaultValue);
}
if (arguments.length > 0) {
var h;
if (1 !== arguments.length || null === o || "object" != typeof o || !("function" != typeof o.encode || o instanceof s) || Array.isArray(o) || o instanceof t.Map || e.isByteBuffer(o) || o instanceof ArrayBuffer || t.Long && o instanceof t.Long) for (l = 0, 
f = arguments.length; l < f; ++l) "undefined" != typeof (h = arguments[l]) && this.$set(r[l].name, h); else this.$set(o);
}
}, o = s.prototype = Object.create(t.Builder.Message.prototype);
o.add = function(e, r, n) {
var s = i._fieldsByName[e];
if (!n) {
if (!s) throw Error(this + "#" + e + " is undefined");
if (!(s instanceof t.Reflect.Message.Field)) throw Error(this + "#" + e + " is not a field: " + s.toString(!0));
if (!s.repeated) throw Error(this + "#" + e + " is not a repeated field");
r = s.verifyValue(r, !0);
}
null === this[e] && (this[e] = []);
this[e].push(r);
return this;
};
o.$add = o.add;
o.set = function(e, r, n) {
if (e && "object" == typeof e) {
n = r;
for (var s in e) e.hasOwnProperty(s) && "undefined" != typeof (r = e[s]) && this.$set(s, r, n);
return this;
}
var o = i._fieldsByName[e];
if (n) this[e] = r; else {
if (!o) throw Error(this + "#" + e + " is not a field: undefined");
if (!(o instanceof t.Reflect.Message.Field)) throw Error(this + "#" + e + " is not a field: " + o.toString(!0));
this[o.name] = r = o.verifyValue(r);
}
if (o && o.oneof) {
var a = this[o.oneof.name];
if (null !== r) {
null !== a && a !== o.name && (this[a] = null);
this[o.oneof.name] = o.name;
} else a === e && (this[o.oneof.name] = null);
}
return this;
};
o.$set = o.set;
o.get = function(e, r) {
if (r) return this[e];
var n = i._fieldsByName[e];
if (!(n && n instanceof t.Reflect.Message.Field)) throw Error(this + "#" + e + " is not a field: undefined");
if (!(n instanceof t.Reflect.Message.Field)) throw Error(this + "#" + e + " is not a field: " + n.toString(!0));
return this[n.name];
};
o.$get = o.get;
for (var a = 0; a < r.length; a++) {
var l = r[a];
l instanceof t.Reflect.Message.ExtensionField || i.builder.options.populateAccessors && function(e) {
var t = e.originalName.replace(/(_[a-zA-Z])/g, function(e) {
return e.toUpperCase().replace("_", "");
});
t = t.substring(0, 1).toUpperCase() + t.substring(1);
var r = e.originalName.replace(/([A-Z])/g, function(e) {
return "_" + e;
}), n = function(t, i) {
this[e.name] = i ? t : e.verifyValue(t);
return this;
}, s = function() {
return this[e.name];
};
null === i.getChild("set" + t) && (o["set" + t] = n);
null === i.getChild("set_" + r) && (o["set_" + r] = n);
null === i.getChild("get" + t) && (o["get" + t] = s);
null === i.getChild("get_" + r) && (o["get_" + r] = s);
}(l);
}
o.encode = function(t, r) {
"boolean" == typeof t && (r = t, t = void 0);
var n = !1;
t || (t = new e(), n = !0);
var s = t.littleEndian;
try {
i.encode(this, t.LE(), r);
return (n ? t.flip() : t).LE(s);
} catch (e) {
t.LE(s);
throw e;
}
};
s.encode = function(e, t, i) {
return new s(e).encode(t, i);
};
o.calculate = function() {
return i.calculate(this);
};
o.encodeDelimited = function(t, r) {
var n = !1;
t || (t = new e(), n = !0);
var s = new e().LE();
i.encode(this, s, r).flip();
t.writeVarint32(s.remaining());
t.append(s);
return n ? t.flip() : t;
};
o.encodeAB = function() {
try {
return this.encode().toArrayBuffer();
} catch (e) {
e.encoded && (e.encoded = e.encoded.toArrayBuffer());
throw e;
}
};
o.toArrayBuffer = o.encodeAB;
o.encodeNB = function() {
try {
return this.encode().toBuffer();
} catch (e) {
e.encoded && (e.encoded = e.encoded.toBuffer());
throw e;
}
};
o.toBuffer = o.encodeNB;
o.encode64 = function() {
try {
return this.encode().toBase64();
} catch (e) {
e.encoded && (e.encoded = e.encoded.toBase64());
throw e;
}
};
o.toBase64 = o.encode64;
o.encodeHex = function() {
try {
return this.encode().toHex();
} catch (e) {
e.encoded && (e.encoded = e.encoded.toHex());
throw e;
}
};
o.toHex = o.encodeHex;
function f(i, r, n, s) {
if (null === i || "object" != typeof i) {
if (s && s instanceof t.Reflect.Enum) {
var o = t.Reflect.Enum.getName(s.object, i);
if (null !== o) return o;
}
return i;
}
if (e.isByteBuffer(i)) return r ? i.toBase64() : i.toBuffer();
if (t.Long.isLong(i)) return n ? i.toString() : t.Long.fromValue(i);
var a;
if (Array.isArray(i)) {
a = [];
i.forEach(function(e, t) {
a[t] = f(e, r, n, s);
});
return a;
}
a = {};
if (i instanceof t.Map) {
for (var l = i.entries(), u = l.next(); !u.done; u = l.next()) a[i.keyElem.valueToString(u.value[0])] = f(u.value[1], r, n, i.valueElem.resolvedType);
return a;
}
var h = i.$type, c = void 0;
for (var p in i) i.hasOwnProperty(p) && (h && (c = h.getChild(p)) ? a[p] = f(i[p], r, n, c.resolvedType) : a[p] = f(i[p], r, n));
return a;
}
o.toRaw = function(e, t) {
return f(this, !!e, !!t, this.$type);
};
o.encodeJSON = function() {
return JSON.stringify(f(this, !0, !0, this.$type));
};
s.decode = function(t, r, n) {
"string" == typeof r && (n = r, r = -1);
"string" == typeof t ? t = e.wrap(t, n || "base64") : e.isByteBuffer(t) || (t = e.wrap(t));
var s = t.littleEndian;
try {
var o = i.decode(t.LE(), r);
t.LE(s);
return o;
} catch (e) {
t.LE(s);
throw e;
}
};
s.decodeDelimited = function(t, r) {
"string" == typeof t ? t = e.wrap(t, r || "base64") : e.isByteBuffer(t) || (t = e.wrap(t));
if (t.remaining() < 1) return null;
var n = t.offset, s = t.readVarint32();
if (t.remaining() < s) {
t.offset = n;
return null;
}
try {
var o = i.decode(t.slice(t.offset, t.offset + s).LE());
t.offset += s;
return o;
} catch (e) {
t.offset += s;
throw e;
}
};
s.decode64 = function(e) {
return s.decode(e, "base64");
};
s.decodeHex = function(e) {
return s.decode(e, "hex");
};
s.decodeJSON = function(e) {
return new s(JSON.parse(e));
};
o.toString = function() {
return i.toString();
};
Object.defineProperty && (Object.defineProperty(s, "$options", {
value: i.buildOpt()
}), Object.defineProperty(o, "$options", {
value: s.$options
}), Object.defineProperty(s, "$type", {
value: i
}), Object.defineProperty(o, "$type", {
value: i
}));
return s;
}(t, this);
this._fields = [];
this._fieldsById = {};
this._fieldsByName = {};
for (var n, s = 0, o = this.children.length; s < o; s++) if ((n = this.children[s]) instanceof y || n instanceof u || n instanceof T) {
if (r.hasOwnProperty(n.name)) throw Error("Illegal reflect child of " + this.toString(!0) + ": " + n.toString(!0) + " cannot override static property '" + n.name + "'");
r[n.name] = n.build();
} else if (n instanceof u.Field) n.build(), this._fields.push(n), this._fieldsById[n.id] = n, 
this._fieldsByName[n.name] = n; else if (!(n instanceof u.OneOf || n instanceof m)) throw Error("Illegal reflect child of " + this.toString(!0) + ": " + this.children[s].toString(!0));
return this.clazz = r;
};
h.encode = function(e, t, i) {
for (var r, n, s = null, o = 0, a = this._fields.length; o < a; ++o) {
n = e[(r = this._fields[o]).name];
r.required && null === n ? null === s && (s = r) : r.encode(i ? n : r.verifyValue(n), t, e);
}
if (null !== s) {
var l = Error("Missing at least one required field for " + this.toString(!0) + ": " + s);
l.encoded = t;
throw l;
}
return t;
};
h.calculate = function(e) {
for (var t, i, r = 0, n = 0, s = this._fields.length; n < s; ++n) {
i = e[(t = this._fields[n]).name];
if (t.required && null === i) throw Error("Missing at least one required field for " + this.toString(!0) + ": " + t);
r += t.calculate(i, e);
}
return r;
};
function c(e, i) {
var r = i.readVarint32(), n = 7 & r, s = r >>> 3;
switch (n) {
case t.WIRE_TYPES.VARINT:
do {
r = i.readUint8();
} while (128 == (128 & r));
break;

case t.WIRE_TYPES.BITS64:
i.offset += 8;
break;

case t.WIRE_TYPES.LDELIM:
r = i.readVarint32();
i.offset += r;
break;

case t.WIRE_TYPES.STARTGROUP:
c(s, i);
break;

case t.WIRE_TYPES.ENDGROUP:
if (s === e) return !1;
throw Error("Illegal GROUPEND after unknown group: " + s + " (" + e + " expected)");

case t.WIRE_TYPES.BITS32:
i.offset += 4;
break;

default:
throw Error("Illegal wire type in unknown group " + e + ": " + n);
}
return !0;
}
h.decode = function(e, i, r) {
"number" != typeof i && (i = -1);
for (var n, s, o, a, l = e.offset, f = new this.clazz(); e.offset < l + i || -1 === i && e.remaining() > 0; ) {
o = (n = e.readVarint32()) >>> 3;
if ((s = 7 & n) === t.WIRE_TYPES.ENDGROUP) {
if (o !== r) throw Error("Illegal group end indicator for " + this.toString(!0) + ": " + o + " (" + (r ? r + " expected" : "not a group") + ")");
break;
}
if (a = this._fieldsById[o]) if (a.repeated && !a.options.packed) f[a.name].push(a.decode(s, e)); else if (a.map) {
var u = a.decode(s, e);
f[a.name].set(u[0], u[1]);
} else {
f[a.name] = a.decode(s, e);
if (a.oneof) {
var h = f[a.oneof.name];
null !== h && h !== a.name && (f[h] = null);
f[a.oneof.name] = a.name;
}
} else switch (s) {
case t.WIRE_TYPES.VARINT:
e.readVarint32();
break;

case t.WIRE_TYPES.BITS32:
e.offset += 4;
break;

case t.WIRE_TYPES.BITS64:
e.offset += 8;
break;

case t.WIRE_TYPES.LDELIM:
var p = e.readVarint32();
e.offset += p;
break;

case t.WIRE_TYPES.STARTGROUP:
for (;c(o, e); ) ;
break;

default:
throw Error("Illegal wire type for unknown field " + o + " in " + this.toString(!0) + "#decode: " + s);
}
}
for (var d = 0, E = this._fields.length; d < E; ++d) if (null === f[(a = this._fields[d]).name]) if ("proto3" === this.syntax) f[a.name] = a.defaultValue; else {
if (a.required) {
var y = Error("Missing at least one required field for " + this.toString(!0) + ": " + a.name);
y.decoded = f;
throw y;
}
t.populateDefaults && null !== a.defaultValue && (f[a.name] = a.defaultValue);
}
return f;
};
i.Message = u;
var p = function(e, i, n, s, o, a, l, f, h, c) {
r.call(this, e, i, a);
this.className = "Message.Field";
this.required = "required" === n;
this.repeated = "repeated" === n;
this.map = "map" === n;
this.keyType = s || null;
this.type = o;
this.resolvedType = null;
this.id = l;
this.options = f || {};
this.defaultValue = null;
this.oneof = h || null;
this.syntax = c || "proto2";
this.originalName = this.name;
this.element = null;
this.keyElement = null;
!this.builder.options.convertFieldsToCamelCase || this instanceof u.ExtensionField || (this.name = t.Util.toCamelCase(this.name));
}, d = p.prototype = Object.create(r.prototype);
d.build = function() {
this.element = new a(this.type, this.resolvedType, !1, this.syntax, this.name);
this.map && (this.keyElement = new a(this.keyType, void 0, !0, this.syntax, this.name));
"proto3" !== this.syntax || this.repeated || this.map ? "undefined" != typeof this.options.default && (this.defaultValue = this.verifyValue(this.options.default)) : this.defaultValue = a.defaultFieldValue(this.type);
};
d.verifyValue = function(e, i) {
i = i || !1;
var r, n = this;
function s(e, t) {
throw Error("Illegal value for " + n.toString(!0) + " of type " + n.type.name + ": " + e + " (" + t + ")");
}
if (null === e) {
this.required && s(typeof e, "required");
"proto3" === this.syntax && this.type !== t.TYPES.message && s(typeof e, "proto3 field without field presence cannot be null");
return null;
}
if (this.repeated && !i) {
Array.isArray(e) || (e = [ e ]);
var o = [];
for (r = 0; r < e.length; r++) o.push(this.element.verifyValue(e[r]));
return o;
}
if (this.map && !i) {
if (e instanceof t.Map) return e;
e instanceof Object || s(typeof e, "expected ProtoBuf.Map or raw object for map field");
return new t.Map(this, e);
}
!this.repeated && Array.isArray(e) && s(typeof e, "no array expected");
return this.element.verifyValue(e);
};
d.hasWirePresence = function(e, i) {
if ("proto3" !== this.syntax) return null !== e;
if (this.oneof && i[this.oneof.name] === this.name) return !0;
switch (this.type) {
case t.TYPES.int32:
case t.TYPES.sint32:
case t.TYPES.sfixed32:
case t.TYPES.uint32:
case t.TYPES.fixed32:
return 0 !== e;

case t.TYPES.int64:
case t.TYPES.sint64:
case t.TYPES.sfixed64:
case t.TYPES.uint64:
case t.TYPES.fixed64:
return 0 !== e.low || 0 !== e.high;

case t.TYPES.bool:
return e;

case t.TYPES.float:
case t.TYPES.double:
return 0 !== e;

case t.TYPES.string:
return e.length > 0;

case t.TYPES.bytes:
return e.remaining() > 0;

case t.TYPES.enum:
return 0 !== e;

case t.TYPES.message:
return null !== e;

default:
return !0;
}
};
d.encode = function(i, r, n) {
if (null === this.type || "object" != typeof this.type) throw Error("[INTERNAL] Unresolved type in " + this.toString(!0) + ": " + this.type);
if (null === i || this.repeated && 0 == i.length) return r;
try {
if (this.repeated) {
var s;
if (this.options.packed && t.PACKABLE_WIRE_TYPES.indexOf(this.type.wireType) >= 0) {
r.writeVarint32(this.id << 3 | t.WIRE_TYPES.LDELIM);
r.ensureCapacity(r.offset += 1);
var o = r.offset;
for (s = 0; s < i.length; s++) this.element.encodeValue(this.id, i[s], r);
var a = r.offset - o, l = e.calculateVarint32(a);
if (l > 1) {
var f = r.slice(o, r.offset);
o += l - 1;
r.offset = o;
r.append(f);
}
r.writeVarint32(a, o - l);
} else for (s = 0; s < i.length; s++) r.writeVarint32(this.id << 3 | this.type.wireType), 
this.element.encodeValue(this.id, i[s], r);
} else if (this.map) i.forEach(function(i, n, s) {
var o = e.calculateVarint32(8 | this.keyType.wireType) + this.keyElement.calculateLength(1, n) + e.calculateVarint32(16 | this.type.wireType) + this.element.calculateLength(2, i);
r.writeVarint32(this.id << 3 | t.WIRE_TYPES.LDELIM);
r.writeVarint32(o);
r.writeVarint32(8 | this.keyType.wireType);
this.keyElement.encodeValue(1, n, r);
r.writeVarint32(16 | this.type.wireType);
this.element.encodeValue(2, i, r);
}, this); else if (this.hasWirePresence(i, n)) {
r.writeVarint32(this.id << 3 | this.type.wireType);
this.element.encodeValue(this.id, i, r);
}
} catch (e) {
throw Error("Illegal value for " + this.toString(!0) + ": " + i + " (" + e + ")");
}
return r;
};
d.calculate = function(i, r) {
i = this.verifyValue(i);
if (null === this.type || "object" != typeof this.type) throw Error("[INTERNAL] Unresolved type in " + this.toString(!0) + ": " + this.type);
if (null === i || this.repeated && 0 == i.length) return 0;
var n = 0;
try {
if (this.repeated) {
var s, o;
if (this.options.packed && t.PACKABLE_WIRE_TYPES.indexOf(this.type.wireType) >= 0) {
n += e.calculateVarint32(this.id << 3 | t.WIRE_TYPES.LDELIM);
o = 0;
for (s = 0; s < i.length; s++) o += this.element.calculateLength(this.id, i[s]);
n += e.calculateVarint32(o);
n += o;
} else for (s = 0; s < i.length; s++) n += e.calculateVarint32(this.id << 3 | this.type.wireType), 
n += this.element.calculateLength(this.id, i[s]);
} else if (this.map) i.forEach(function(i, r, s) {
var o = e.calculateVarint32(8 | this.keyType.wireType) + this.keyElement.calculateLength(1, r) + e.calculateVarint32(16 | this.type.wireType) + this.element.calculateLength(2, i);
n += e.calculateVarint32(this.id << 3 | t.WIRE_TYPES.LDELIM);
n += e.calculateVarint32(o);
n += o;
}, this); else if (this.hasWirePresence(i, r)) {
n += e.calculateVarint32(this.id << 3 | this.type.wireType);
n += this.element.calculateLength(this.id, i);
}
} catch (e) {
throw Error("Illegal value for " + this.toString(!0) + ": " + i + " (" + e + ")");
}
return n;
};
d.decode = function(e, i, r) {
var n, s;
if (!(!this.map && e == this.type.wireType || !r && this.repeated && this.options.packed && e == t.WIRE_TYPES.LDELIM || this.map && e == t.WIRE_TYPES.LDELIM)) throw Error("Illegal wire type for field " + this.toString(!0) + ": " + e + " (" + this.type.wireType + " expected)");
if (e == t.WIRE_TYPES.LDELIM && this.repeated && this.options.packed && t.PACKABLE_WIRE_TYPES.indexOf(this.type.wireType) >= 0 && !r) {
s = i.readVarint32();
s = i.offset + s;
for (var o = []; i.offset < s; ) o.push(this.decode(this.type.wireType, i, !0));
return o;
}
if (this.map) {
var l = a.defaultFieldValue(this.keyType);
n = a.defaultFieldValue(this.type);
s = i.readVarint32();
if (i.remaining() < s) throw Error("Illegal number of bytes for " + this.toString(!0) + ": " + s + " required but got only " + i.remaining());
var f = i.clone();
f.limit = f.offset + s;
i.offset += s;
for (;f.remaining() > 0; ) {
var u = f.readVarint32();
e = 7 & u;
var h = u >>> 3;
if (1 === h) l = this.keyElement.decode(f, e, h); else {
if (2 !== h) throw Error("Unexpected tag in map field key/value submessage");
n = this.element.decode(f, e, h);
}
}
return [ l, n ];
}
return this.element.decode(i, e, this.id);
};
i.Message.Field = p;
var E = function(e, t, i, r, n, s, o) {
p.call(this, e, t, i, null, r, n, s, o);
this.extension;
};
E.prototype = Object.create(p.prototype);
i.Message.ExtensionField = E;
i.Message.OneOf = function(e, t, i) {
r.call(this, e, t, i);
this.fields = [];
};
var y = function(e, t, i, r, n) {
s.call(this, e, t, i, r, n);
this.className = "Enum";
this.object = null;
};
y.getName = function(e, t) {
for (var i, r = Object.keys(e), n = 0; n < r.length; ++n) if (e[i = r[n]] === t) return i;
return null;
};
(y.prototype = Object.create(s.prototype)).build = function(e) {
if (this.object && !e) return this.object;
for (var i = new t.Builder.Enum(), r = this.getChildren(y.Value), n = 0, s = r.length; n < s; ++n) i[r[n].name] = r[n].id;
Object.defineProperty && Object.defineProperty(i, "$options", {
value: this.buildOpt(),
enumerable: !1
});
return this.object = i;
};
i.Enum = y;
var g = function(e, t, i, n) {
r.call(this, e, t, i);
this.className = "Enum.Value";
this.id = n;
};
g.prototype = Object.create(r.prototype);
i.Enum.Value = g;
var m = function(e, t, i, n) {
r.call(this, e, t, i);
this.field = n;
};
m.prototype = Object.create(r.prototype);
i.Extension = m;
var T = function(e, t, i, r) {
s.call(this, e, t, i, r);
this.className = "Service";
this.clazz = null;
};
(T.prototype = Object.create(s.prototype)).build = function(i) {
return this.clazz && !i ? this.clazz : this.clazz = function(t, i) {
for (var r = function(e) {
t.Builder.Service.call(this);
this.rpcImpl = e || function(e, t, i) {
setTimeout(i.bind(this, Error("Not implemented, see: https://github.com/dcodeIO/ProtoBuf.js/wiki/Services")), 0);
};
}, n = r.prototype = Object.create(t.Builder.Service.prototype), s = i.getChildren(t.Reflect.Service.RPCMethod), o = 0; o < s.length; o++) (function(t) {
n[t.name] = function(r, n) {
try {
try {
r = t.resolvedRequestType.clazz.decode(e.wrap(r));
} catch (e) {
if (!(e instanceof TypeError)) throw e;
}
if (null === r || "object" != typeof r) throw Error("Illegal arguments");
r instanceof t.resolvedRequestType.clazz || (r = new t.resolvedRequestType.clazz(r));
this.rpcImpl(t.fqn(), r, function(e, r) {
if (e) n(e); else {
null === r && (r = "");
try {
r = t.resolvedResponseType.clazz.decode(r);
} catch (e) {}
r && r instanceof t.resolvedResponseType.clazz ? n(null, r) : n(Error("Illegal response type received in service method " + i.name + "#" + t.name));
}
});
} catch (e) {
setTimeout(n.bind(this, e), 0);
}
};
r[t.name] = function(e, i, n) {
new r(e)[t.name](i, n);
};
Object.defineProperty && (Object.defineProperty(r[t.name], "$options", {
value: t.buildOpt()
}), Object.defineProperty(n[t.name], "$options", {
value: r[t.name].$options
}));
})(s[o]);
Object.defineProperty && (Object.defineProperty(r, "$options", {
value: i.buildOpt()
}), Object.defineProperty(n, "$options", {
value: r.$options
}), Object.defineProperty(r, "$type", {
value: i
}), Object.defineProperty(n, "$type", {
value: i
}));
return r;
}(t, this);
};
i.Service = T;
var v = function(e, t, i, n) {
r.call(this, e, t, i);
this.className = "Service.Method";
this.options = n || {};
};
(v.prototype = Object.create(r.prototype)).buildOpt = o.buildOpt;
i.Service.Method = v;
var S = function(e, t, i, r, n, s, o, a) {
v.call(this, e, t, i, a);
this.className = "Service.RPCMethod";
this.requestName = r;
this.responseName = n;
this.requestStream = s;
this.responseStream = o;
this.resolvedRequestType = null;
this.resolvedResponseType = null;
};
S.prototype = Object.create(v.prototype);
i.Service.RPCMethod = S;
return i;
}(i);
i.Builder = function(e, t, i) {
var r = function(e) {
this.ns = new i.Namespace(this, null, "");
this.ptr = this.ns;
this.resolved = !1;
this.result = null;
this.files = {};
this.importRoot = null;
this.options = e || {};
}, n = r.prototype;
r.isMessage = function(e) {
return "string" == typeof e.name && ("undefined" == typeof e.values && "undefined" == typeof e.rpc);
};
r.isMessageField = function(e) {
return "string" == typeof e.rule && "string" == typeof e.name && "string" == typeof e.type && "undefined" != typeof e.id;
};
r.isEnum = function(e) {
return "string" == typeof e.name && !("undefined" == typeof e.values || !Array.isArray(e.values) || 0 === e.values.length);
};
r.isService = function(e) {
return !("string" != typeof e.name || "object" != typeof e.rpc || !e.rpc);
};
r.isExtend = function(e) {
return "string" == typeof e.ref;
};
n.reset = function() {
this.ptr = this.ns;
return this;
};
n.define = function(e) {
if ("string" != typeof e || !t.TYPEREF.test(e)) throw Error("illegal namespace: " + e);
e.split(".").forEach(function(e) {
var t = this.ptr.getChild(e);
null === t && this.ptr.addChild(t = new i.Namespace(this, this.ptr, e));
this.ptr = t;
}, this);
return this;
};
n.create = function(t) {
if (!t) return this;
if (Array.isArray(t)) {
if (0 === t.length) return this;
t = t.slice();
} else t = [ t ];
for (var n = [ t ]; n.length > 0; ) {
t = n.pop();
if (!Array.isArray(t)) throw Error("not a valid namespace: " + JSON.stringify(t));
for (;t.length > 0; ) {
var s = t.shift();
if (r.isMessage(s)) {
var o = new i.Message(this, this.ptr, s.name, s.options, s.isGroup, s.syntax), a = {};
s.oneofs && Object.keys(s.oneofs).forEach(function(e) {
o.addChild(a[e] = new i.Message.OneOf(this, o, e));
}, this);
s.fields && s.fields.forEach(function(e) {
if (null !== o.getChild(0 | e.id)) throw Error("duplicate or invalid field id in " + o.name + ": " + e.id);
if (e.options && "object" != typeof e.options) throw Error("illegal field options in " + o.name + "#" + e.name);
var t = null;
if ("string" == typeof e.oneof && !(t = a[e.oneof])) throw Error("illegal oneof in " + o.name + "#" + e.name + ": " + e.oneof);
e = new i.Message.Field(this, o, e.rule, e.keytype, e.type, e.name, e.id, e.options, t, s.syntax);
t && t.fields.push(e);
o.addChild(e);
}, this);
var l = [];
s.enums && s.enums.forEach(function(e) {
l.push(e);
});
s.messages && s.messages.forEach(function(e) {
l.push(e);
});
s.services && s.services.forEach(function(e) {
l.push(e);
});
s.extensions && ("number" == typeof s.extensions[0] ? o.extensions = [ s.extensions ] : o.extensions = s.extensions);
this.ptr.addChild(o);
if (l.length > 0) {
n.push(t);
t = l;
l = null;
this.ptr = o;
o = null;
continue;
}
l = null;
} else if (r.isEnum(s)) {
o = new i.Enum(this, this.ptr, s.name, s.options, s.syntax);
s.values.forEach(function(e) {
o.addChild(new i.Enum.Value(this, o, e.name, e.id));
}, this);
this.ptr.addChild(o);
} else if (r.isService(s)) {
o = new i.Service(this, this.ptr, s.name, s.options);
Object.keys(s.rpc).forEach(function(e) {
var t = s.rpc[e];
o.addChild(new i.Service.RPCMethod(this, o, e, t.request, t.response, !!t.request_stream, !!t.response_stream, t.options));
}, this);
this.ptr.addChild(o);
} else {
if (!r.isExtend(s)) throw Error("not a valid definition: " + JSON.stringify(s));
if (o = this.ptr.resolve(s.ref, !0)) s.fields.forEach(function(t) {
if (null !== o.getChild(0 | t.id)) throw Error("duplicate extended field id in " + o.name + ": " + t.id);
if (o.extensions) {
var r = !1;
o.extensions.forEach(function(e) {
t.id >= e[0] && t.id <= e[1] && (r = !0);
});
if (!r) throw Error("illegal extended field id in " + o.name + ": " + t.id + " (not within valid ranges)");
}
var n = t.name;
this.options.convertFieldsToCamelCase && (n = e.Util.toCamelCase(n));
var s = new i.Message.ExtensionField(this, o, t.rule, t.type, this.ptr.fqn() + "." + n, t.id, t.options), a = new i.Extension(this, this.ptr, t.name, s);
s.extension = a;
this.ptr.addChild(a);
o.addChild(s);
}, this); else if (!/\.?google\.protobuf\./.test(s.ref)) throw Error("extended message " + s.ref + " is not defined");
}
s = null;
o = null;
}
t = null;
this.ptr = this.ptr.parent;
}
this.resolved = !1;
this.result = null;
return this;
};
function s(e) {
e.messages && e.messages.forEach(function(t) {
t.syntax = e.syntax;
s(t);
});
e.enums && e.enums.forEach(function(t) {
t.syntax = e.syntax;
});
}
n.import = function(t, i) {
var r = "/";
if ("string" == typeof i) {
e.Util.IS_NODE && (i = require("path").resolve(i));
if (!0 === this.files[i]) return this.reset();
this.files[i] = !0;
} else if ("object" == typeof i) {
var n = i.root;
e.Util.IS_NODE && (n = require("path").resolve(n));
(n.indexOf("\\") >= 0 || i.file.indexOf("\\") >= 0) && (r = "\\");
var o = n + r + i.file;
if (!0 === this.files[o]) return this.reset();
this.files[o] = !0;
}
if (t.imports && t.imports.length > 0) {
var a, l = !1;
if ("object" == typeof i) {
this.importRoot = i.root;
l = !0;
a = this.importRoot;
i = i.file;
(a.indexOf("\\") >= 0 || i.indexOf("\\") >= 0) && (r = "\\");
} else if ("string" == typeof i) if (this.importRoot) a = this.importRoot; else if (i.indexOf("/") >= 0) "" === (a = i.replace(/\/[^\/]*$/, "")) && (a = "/"); else if (i.indexOf("\\") >= 0) {
a = i.replace(/\\[^\\]*$/, "");
r = "\\";
} else a = "."; else a = null;
for (var f = 0; f < t.imports.length; f++) if ("string" == typeof t.imports[f]) {
if (!a) throw Error("cannot determine import root");
var u = t.imports[f];
if ("google/protobuf/descriptor.proto" === u) continue;
u = a + r + u;
if (!0 === this.files[u]) continue;
/\.proto$/i.test(u) && !e.DotProto && (u = u.replace(/\.proto$/, ".json"));
var h = e.Util.fetch(u);
if (null === h) throw Error("failed to import '" + u + "' in '" + i + "': file not found");
/\.json$/i.test(u) ? this.import(JSON.parse(h + ""), u) : this.import(e.DotProto.Parser.parse(h), u);
} else i ? /\.(\w+)$/.test(i) ? this.import(t.imports[f], i.replace(/^(.+)\.(\w+)$/, function(e, t, i) {
return t + "_import" + f + "." + i;
})) : this.import(t.imports[f], i + "_import" + f) : this.import(t.imports[f]);
l && (this.importRoot = null);
}
t.package && this.define(t.package);
t.syntax && s(t);
var c = this.ptr;
t.options && Object.keys(t.options).forEach(function(e) {
c.options[e] = t.options[e];
});
t.messages && (this.create(t.messages), this.ptr = c);
t.enums && (this.create(t.enums), this.ptr = c);
t.services && (this.create(t.services), this.ptr = c);
t.extends && this.create(t.extends);
return this.reset();
};
n.resolveAll = function() {
var r;
if (null == this.ptr || "object" == typeof this.ptr.type) return this;
if (this.ptr instanceof i.Namespace) this.ptr.children.forEach(function(e) {
this.ptr = e;
this.resolveAll();
}, this); else if (this.ptr instanceof i.Message.Field) {
if (t.TYPE.test(this.ptr.type)) this.ptr.type = e.TYPES[this.ptr.type]; else {
if (!t.TYPEREF.test(this.ptr.type)) throw Error("illegal type reference in " + this.ptr.toString(!0) + ": " + this.ptr.type);
if (!(r = (this.ptr instanceof i.Message.ExtensionField ? this.ptr.extension.parent : this.ptr.parent).resolve(this.ptr.type, !0))) throw Error("unresolvable type reference in " + this.ptr.toString(!0) + ": " + this.ptr.type);
this.ptr.resolvedType = r;
if (r instanceof i.Enum) {
this.ptr.type = e.TYPES.enum;
if ("proto3" === this.ptr.syntax && "proto3" !== r.syntax) throw Error("proto3 message cannot reference proto2 enum");
} else {
if (!(r instanceof i.Message)) throw Error("illegal type reference in " + this.ptr.toString(!0) + ": " + this.ptr.type);
this.ptr.type = r.isGroup ? e.TYPES.group : e.TYPES.message;
}
}
if (this.ptr.map) {
if (!t.TYPE.test(this.ptr.keyType)) throw Error("illegal key type for map field in " + this.ptr.toString(!0) + ": " + this.ptr.keyType);
this.ptr.keyType = e.TYPES[this.ptr.keyType];
}
} else if (this.ptr instanceof e.Reflect.Service.Method) {
if (!(this.ptr instanceof e.Reflect.Service.RPCMethod)) throw Error("illegal service type in " + this.ptr.toString(!0));
if (!((r = this.ptr.parent.resolve(this.ptr.requestName, !0)) && r instanceof e.Reflect.Message)) throw Error("Illegal type reference in " + this.ptr.toString(!0) + ": " + this.ptr.requestName);
this.ptr.resolvedRequestType = r;
if (!((r = this.ptr.parent.resolve(this.ptr.responseName, !0)) && r instanceof e.Reflect.Message)) throw Error("Illegal type reference in " + this.ptr.toString(!0) + ": " + this.ptr.responseName);
this.ptr.resolvedResponseType = r;
} else if (!(this.ptr instanceof e.Reflect.Message.OneOf || this.ptr instanceof e.Reflect.Extension || this.ptr instanceof e.Reflect.Enum.Value)) throw Error("illegal object in namespace: " + typeof this.ptr + ": " + this.ptr);
return this.reset();
};
n.build = function(e) {
this.reset();
this.resolved || (this.resolveAll(), this.resolved = !0, this.result = null);
null === this.result && (this.result = this.ns.build());
if (!e) return this.result;
for (var t = "string" == typeof e ? e.split(".") : e, i = this.result, r = 0; r < t.length; r++) {
if (!i[t[r]]) {
i = null;
break;
}
i = i[t[r]];
}
return i;
};
n.lookup = function(e, t) {
return e ? this.ns.resolve(e, t) : this.ns;
};
n.toString = function() {
return "Builder";
};
r.Message = function() {};
r.Enum = function() {};
r.Service = function() {};
return r;
}(i, i.Lang, i.Reflect);
i.Map = function(e, t) {
var i = function(e, i) {
if (!e.map) throw Error("field is not a map");
this.field = e;
this.keyElem = new t.Element(e.keyType, null, !0, e.syntax);
this.valueElem = new t.Element(e.type, e.resolvedType, !1, e.syntax);
this.map = {};
Object.defineProperty(this, "size", {
get: function() {
return Object.keys(this.map).length;
}
});
if (i) for (var r = Object.keys(i), n = 0; n < r.length; n++) {
var s = this.keyElem.valueFromString(r[n]), o = this.valueElem.verifyValue(i[r[n]]);
this.map[this.keyElem.valueToString(s)] = {
key: s,
value: o
};
}
}, r = i.prototype;
function n(e) {
var t = 0;
return {
next: function() {
return t < e.length ? {
done: !1,
value: e[t++]
} : {
done: !0
};
}
};
}
r.clear = function() {
this.map = {};
};
r.delete = function(e) {
var t = this.keyElem.valueToString(this.keyElem.verifyValue(e)), i = t in this.map;
delete this.map[t];
return i;
};
r.entries = function() {
for (var e, t = [], i = Object.keys(this.map), r = 0; r < i.length; r++) t.push([ (e = this.map[i[r]]).key, e.value ]);
return n(t);
};
r.keys = function() {
for (var e = [], t = Object.keys(this.map), i = 0; i < t.length; i++) e.push(this.map[t[i]].key);
return n(e);
};
r.values = function() {
for (var e = [], t = Object.keys(this.map), i = 0; i < t.length; i++) e.push(this.map[t[i]].value);
return n(e);
};
r.forEach = function(e, t) {
for (var i, r = Object.keys(this.map), n = 0; n < r.length; n++) e.call(t, (i = this.map[r[n]]).value, i.key, this);
};
r.set = function(e, t) {
var i = this.keyElem.verifyValue(e), r = this.valueElem.verifyValue(t);
this.map[this.keyElem.valueToString(i)] = {
key: i,
value: r
};
return this;
};
r.get = function(e) {
var t = this.keyElem.valueToString(this.keyElem.verifyValue(e));
if (t in this.map) return this.map[t].value;
};
r.has = function(e) {
return this.keyElem.valueToString(this.keyElem.verifyValue(e)) in this.map;
};
return i;
}(0, i.Reflect);
i.loadProto = function(e, t, r) {
("string" == typeof t || t && "string" == typeof t.file && "string" == typeof t.root) && (r = t, 
t = void 0);
return i.loadJson(i.DotProto.Parser.parse(e), t, r);
};
i.protoFromString = i.loadProto;
i.loadProtoFile = function(e, t, r) {
t && "object" == typeof t ? (r = t, t = null) : t && "function" == typeof t || (t = null);
if (t) return i.Util.fetch("string" == typeof e ? e : e.root + "/" + e.file, function(n) {
if (null !== n) try {
t(null, i.loadProto(n, r, e));
} catch (e) {
t(e);
} else t(Error("Failed to fetch file"));
});
var n = i.Util.fetch("object" == typeof e ? e.root + "/" + e.file : e);
return null === n ? null : i.loadProto(n, r, e);
};
i.protoFromFile = i.loadProtoFile;
i.newBuilder = function(e) {
"undefined" == typeof (e = e || {}).convertFieldsToCamelCase && (e.convertFieldsToCamelCase = i.convertFieldsToCamelCase);
"undefined" == typeof e.populateAccessors && (e.populateAccessors = i.populateAccessors);
return new i.Builder(e);
};
i.loadJson = function(e, t, r) {
("string" == typeof t || t && "string" == typeof t.file && "string" == typeof t.root) && (r = t, 
t = null);
t && "object" == typeof t || (t = i.newBuilder());
"string" == typeof e && (e = JSON.parse(e));
t.import(e, r);
t.resolveAll();
return t;
};
i.loadJsonFile = function(e, t, r) {
t && "object" == typeof t ? (r = t, t = null) : t && "function" == typeof t || (t = null);
if (t) return i.Util.fetch("string" == typeof e ? e : e.root + "/" + e.file, function(n) {
if (null !== n) try {
t(null, i.loadJson(JSON.parse(n), r, e));
} catch (e) {
t(e);
} else t(Error("Failed to fetch file"));
});
var n = i.Util.fetch("object" == typeof e ? e.root + "/" + e.file : e);
return null === n ? null : i.loadJson(JSON.parse(n), r, e);
};
return i;
});
})();