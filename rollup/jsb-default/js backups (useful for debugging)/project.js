window.__require = function e(i, t, s) {
function c(n, a) {
if (!t[n]) {
if (!i[n]) {
var r = n.split("/");
r = r[r.length - 1];
if (!i[r]) {
var h = "function" == typeof __require && __require;
if (!a && h) return h(r, !0);
if (o) return o(r, !0);
throw new Error("Cannot find module '" + n + "'");
}
}
var d = t[n] = {
exports: {}
};
i[n][0].call(d.exports, function(e) {
return c(i[n][1][e] || e);
}, d, d.exports, e, i, t, s);
}
return t[n].exports;
}
for (var o = "function" == typeof __require && __require, n = 0; n < s.length; n++) c(s[n]);
return c;
}({
RankingView: [ function(e, i, t) {
"use strict";
cc._RF.push(i, "d45f4Qrfe1CRoZGfgaTBEt7", "RankingView");
cc.Class({
extends: cc.Component,
name: "RankingView",
properties: {
rankingScrollView: cc.Sprite
},
onLoad: function() {},
start: function() {
if (CC_WECHATGAME) {
window.wx.showShareMenu({
withShareTicket: !0
});
window.wx.postMessage({
messageType: 1,
MAIN_MENU_NUM: "x1"
});
}
},
friendButtonFunc: function(e) {
CC_WECHATGAME ? window.wx.postMessage({
messageType: 1,
MAIN_MENU_NUM: "x1"
}) : cc.log("获取好友排行榜数据。x1");
},
groupFriendButtonFunc: function(e) {
CC_WECHATGAME ? window.wx.shareAppMessage({
success: function(e) {
void 0 != e.shareTickets && e.shareTickets.length > 0 && window.wx.postMessage({
messageType: 5,
MAIN_MENU_NUM: "x1",
shareTicket: e.shareTickets[0]
});
}
}) : cc.log("获取群排行榜数据。x1");
},
gameOverButtonFunc: function(e) {
CC_WECHATGAME ? window.wx.postMessage({
messageType: 4,
MAIN_MENU_NUM: "x1"
}) : cc.log("获取横向展示排行榜数据。x1");
},
gameagain: function() {
cc.director.loadScene("rolling");
},
gamebegin: function() {
cc.director.loadScene("gamebegin");
}
});
cc._RF.pop();
}, {} ],
gameagain: [ function(e, i, t) {
"use strict";
cc._RF.push(i, "a8952/UufFFFbECdLuXzs/k", "gameagain");
cc.Class({
extends: cc.Component,
properties: {
rankview: cc.Node
},
onLoad: function() {},
game_home: function() {
cc.director.loadScene("gamebegin");
cc.audioEngine.stopAllEffects();
},
game_again: function() {
cc.director.loadScene("rolling");
cc.audioEngine.stopAllEffects();
},
paihangbang: function() {
cc.director.loadScene("ranking");
},
start: function() {}
});
cc._RF.pop();
}, {} ],
gamebegin: [ function(e, i, t) {
"use strict";
cc._RF.push(i, "3e288+FleJDS62JjpOeLaIQ", "gamebegin");
cc.Class({
extends: cc.Component,
properties: {
roll: [ cc.Node ],
player: cc.Node,
light: cc.Node,
light0: cc.Node
},
onLoad: function() {
if (CC_WECHATGAME) {
wx.showShareMenu();
cc.loader.loadRes("texture/share", function(e, i) {
wx.onShareAppMessage(function(e) {
return {
title: "我发现了一个很好玩的小游戏，赶紧一起来滚吧~",
imageUrl: i.url,
success: function(e) {
console.log("转发成功!!!");
common.diamond += 20;
},
fail: function(e) {
console.log("转发失败!!!");
}
};
});
});
}
var e = cc.moveTo(3, -158, this.roll[0].y), i = cc.moveTo(3, 0, this.roll[0].y), t = cc.moveTo(3, 158, this.roll[0].y), s = cc.rotateBy(3, 720), c = cc.rotateBy(3, 720), o = cc.rotateBy(3, 720), n = cc.moveBy(4, cc.view.getVisibleSize().width + 200, 0), a = cc.repeatForever(n);
this.roll[0].runAction(e);
this.roll[1].runAction(i);
this.roll[2].runAction(t);
this.roll[0].runAction(s);
this.roll[1].runAction(c);
this.roll[2].runAction(o);
this.player.runAction(a);
var r = cc.repeatForever(cc.sequence(cc.fadeIn(1), cc.fadeOut(1), cc.rotateBy(2, 360))), h = cc.repeatForever(cc.sequence(cc.rotateBy(2, 360), cc.fadeIn(1), cc.fadeOut(1)));
this.light.runAction(r);
this.light0.runAction(h);
},
game_again: function() {
cc.director.loadScene("rolling");
},
paihangbang: function() {
cc.director.loadScene("ranking");
},
share: function() {
CC_WECHATGAME && cc.loader.loadRes("texture/share", function(e, i) {
wx.shareAppMessage({
title: "我发现了一个很好玩的小游戏，赶紧一起来滚吧~",
imageUrl: i.url,
success: function(e) {
console.log("转发成功!!!");
},
fail: function(e) {
console.log("转发失败!!!");
}
});
});
},
start: function() {},
update: function(e) {
if (this.player.x > 500) {
this.player.setPosition(-cc.view.getVisibleSize().width / 2, this.player.y);
var i = cc.rotateBy(2, 360), t = cc.rotateBy(2, 360), s = cc.rotateBy(2, 360);
this.roll[0].runAction(i);
this.roll[1].runAction(t);
this.roll[2].runAction(s);
}
}
});
cc._RF.pop();
}, {} ],
maingame: [ function(e, i, t) {
"use strict";
cc._RF.push(i, "4ee72139OhEBrZmKB+VQxfE", "maingame");
cc.Class({
extends: cc.Component,
properties: {
speed: 0,
player: cc.Node,
target: cc.Node,
tips: cc.Node,
colorPrefab: cc.Prefab,
_time: 0,
_range: cc.v2(0, 0),
_acc: cc.v2(0, 0),
xSpeed: 0,
ySpeed: 0,
acc: 1
},
onLoad: function() {
if (CC_WECHATGAME) {
wx.showShareMenu();
cc.loader.loadRes("texture/share", function(e, i) {
wx.onShareAppMessage(function(e) {
return {
title: "我发现了一个很好玩的小游戏，赶紧一起来滚吧~",
imageUrl: i.url,
success: function(e) {
console.log("转发成功!!!");
common.diamond += 20;
},
fail: function(e) {
console.log("转发失败!!!");
}
};
});
});
}
this.target.zIndex = 2e3;
this.accUp = !1;
this.accDown = !1;
this.accLeft = !1;
this.accRight = !1;
var e = cc.view.getVisibleSize();
this._range.x = e.width / 2 - this.target.width / 2;
this._range.y = e.height / 2 - this.target.height / 2;
cc.systemEvent.setAccelerometerEnabled(!0);
cc.systemEvent.on(cc.SystemEvent.EventType.DEVICEMOTION, this.onDeviceMotionEvent, this);
cc.systemEvent.on(cc.SystemEvent.EventType.KEY_DOWN, this.onKeyDown, this);
cc.systemEvent.on(cc.SystemEvent.EventType.KEY_UP, this.onKeyUp, this);
var i = cc.fadeOut(1);
this.acc = 0;
setTimeout(function() {
this.tips.runAction(i);
}.bind(this), 1e3);
setTimeout(function() {
this.player.active = "true";
this.acc = 1;
}.bind(this), 2e3);
},
onDestroy: function() {
cc.systemEvent.setAccelerometerEnabled(!1);
cc.systemEvent.off(cc.SystemEvent.EventType.DEVICEMOTION, this.onDeviceMotionEvent, this);
cc.systemEvent.off(cc.SystemEvent.EventType.KEY_DOWN, this.onKeyDown, this);
cc.systemEvent.off(cc.SystemEvent.EventType.KEY_UP, this.onKeyUp, this);
},
onKeyDown: function(e) {
switch (e.keyCode) {
case cc.macro.KEY.w:
this.ySpeed = 100;
break;

case cc.macro.KEY.s:
this.ySpeed = -100;
break;

case cc.macro.KEY.a:
this.xSpeed = -100;
break;

case cc.macro.KEY.d:
this.xSpeed = 100;
}
},
onKeyUp: function(e) {
switch (e.keyCode) {
case cc.macro.KEY.w:
case cc.macro.KEY.s:
this.ySpeed = 0;
break;

case cc.macro.KEY.a:
case cc.macro.KEY.d:
this.xSpeed = 0;
}
},
onDeviceMotionEvent: function(e) {
if (!!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)) {
this._acc.x = 2 * -e.acc.x;
this._acc.y = 2 * -e.acc.y;
} else {
this._acc.x = 2 * e.acc.x;
this._acc.y = 2 * e.acc.y;
}
},
update: function(e) {
this.target.x += e * this.xSpeed * this.acc;
this.target.y += e * this.ySpeed * this.acc;
this.target.x += this._acc.x * e * (this.speed + this._time) * this.acc;
this.target.y += this._acc.y * e * (this.speed + this._time) * this.acc;
this.node.setPosition(-this.target.x, -this.target.y);
}
});
cc._RF.pop();
}, {} ],
playerjs: [ function(e, i, t) {
"use strict";
cc._RF.push(i, "6fc91c54xJIoZHyi/5WR3CH", "playerjs");
cc.Class({
extends: cc.Component,
properties: {
bg: cc.Node,
bridge: [],
allbridge: [],
colorPrefab: [ cc.Prefab ],
beachPrefab: [ cc.Prefab ],
gardenPrefab: [ cc.Prefab ],
bridgePrefab: [ cc.Prefab ],
brickPrefab: [ cc.Prefab ],
roadPrefab: [ cc.Prefab ],
scene: [ cc.Prefab ],
startbridge: cc.Node,
isend: 0,
is_end: 0,
lastbridge: cc.Node,
thisbridge: cc.Node,
prebridge: cc.Node,
score: 0,
checkout: cc.Node,
scorelabel: cc.Node,
scoreend: cc.Node,
timeout: 0,
lastorder: 0,
thisorder: 0,
random_step: 0,
random_group: 0,
random_position: 0,
tipstring_num: 0,
bridge_class: 0,
last_bridge_class: 0,
newbridge: [],
dis_a: 0,
dis_b: 0,
colorAudio: {
type: cc.AudioClip,
default: []
},
gardenAudio: {
type: cc.AudioClip,
default: null
},
beachAudio: {
type: cc.AudioClip,
default: null
},
bridgeAudio: {
type: cc.AudioClip,
default: null
},
brickAudio: {
type: cc.AudioClip,
default: null
},
carAudio: {
type: cc.AudioClip,
default: null
},
endAudio: {
type: cc.AudioClip,
default: null
},
nodex: 0,
nodey: 0,
tipslabel: cc.Node,
tipslabel1: cc.Node
},
onLoad: function() {
cc.director.getCollisionManager().enabled = !0;
this.random_step = Math.floor(4 * Math.random()) + 1;
this.bridge = [ this.colorPrefab, this.beachPrefab, this.gardenPrefab, this.bridgePrefab, this.brickPrefab, this.roadPrefab ];
this.startbridge.order = 1;
this.startbridge.out = 3;
this.thisbridge = this.startbridge;
this.dis_a = 138;
this.dis_b = 80;
this.nodex = 0;
this.nodey = 0;
this.random_group = Math.floor(5 * Math.random()) + 5;
this.random_position = Math.floor(Math.random() * (this.random_group - 3)) + 2;
this.newbridge = this.bridge[0];
this.score = -1;
this.allbridge = [];
},
onCollisionEnter: function(e, i) {
var t = cc.tintTo(2, 255, 254, 203), s = cc.tintTo(2, 204, 204, 204), c = cc.tintTo(2, 204, 255, 154), o = cc.tintTo(2, 254, 204, 203), n = cc.tintTo(2, 204, 255, 255), a = cc.tintTo(2, 204, 204, 254), r = cc.fadeIn(1), h = cc.fadeIn(1), d = cc.fadeOut(1), l = cc.fadeOut(1);
this.lastbridge && (this.lastorder = this.lastbridge.order);
this.thisbridge && (this.thisorder = this.thisbridge.order);
switch (e.node.name) {
case "color0":
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.colorAudio[0], !1);
this.node.parent.getComponent("maingame").speed = 300;
break;

case "color1":
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.colorAudio[1], !1);
this.node.parent.getComponent("maingame").speed = 300;
break;

case "color2":
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.colorAudio[2], !1);
this.node.parent.getComponent("maingame").speed = 300;
break;

case "color3":
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.colorAudio[3], !1);
this.node.parent.getComponent("maingame").speed = 300;
break;

case "color4":
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.colorAudio[4], !1);
this.node.parent.getComponent("maingame").speed = 300;
break;

case "color5":
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.colorAudio[5], !1);
this.node.parent.getComponent("maingame").speed = 300;
break;

case "beach0":
this.node.parent.getComponent("maingame").speed = 200;
break;

case "garden0":
case "bridge0":
case "bridge1":
case "brick0":
case "brick1":
this.node.parent.getComponent("maingame").speed = 300;
break;

case "road0":
case "road1":
case "road2":
this.node.parent.getComponent("maingame").speed = 450;
}
6 == this.tipstring_num && (this.tipstring_num = 0);
0 == this.bridge_class && this.bg.runAction(t);
if (1 == this.bridge_class && this.last_bridge_class != this.bridge_class) {
this.bg.runAction(s);
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.beachAudio, !0);
}
if (2 == this.bridge_class && this.last_bridge_class != this.bridge_class) {
this.bg.runAction(c);
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.gardenAudio, !0);
}
if (3 == this.bridge_class && this.last_bridge_class != this.bridge_class) {
this.bg.runAction(o);
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.bridgeAudio, !0);
}
if (4 == this.bridge_class && this.last_bridge_class != this.bridge_class) {
this.bg.runAction(n);
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.brickAudio, !0);
}
if (5 == this.bridge_class && this.last_bridge_class != this.bridge_class) {
this.bg.runAction(a);
cc.audioEngine.stopAllEffects();
cc.audioEngine.playEffect(this.carAudio, !0);
}
if (e.node.order - 2 == this.score) {
4 == e.node.order && this.startbridge.runAction(d);
this.lastbridge = this.thisbridge;
this.thisbridge = e.node;
var g = 1, u = !1;
u = 3 == this.bridge_class;
this.last_bridge_class = this.bridge_class;
if (0 == this.random_group) {
this.bridge_class = Math.floor(6 * Math.random());
this.newbridge = this.bridge[this.bridge_class];
this.tipslabel.getComponent(cc.Label).string = [ "重力控制小球滚动", "你给我有多远滚多远", "你让我滚，我滚了", "你让我回来，对不起，滚远了", "思想有多远，你给我滚多远", "光速有多快，你给我滚多快" ][this.tipstring_num];
this.tipstring_num++;
this.random_group = Math.floor(5 * Math.random()) + 5;
this.random_position = Math.floor(Math.random() * (this.random_group - 3)) + 2;
}
if (u && 3 == this.bridge_class) {
this.dis_a = 63;
this.dis_b = 38;
} else if (u && 3 != this.bridge_class) {
this.dis_a = 97;
this.dis_b = 54;
} else if (u || 3 != this.bridge_class) {
this.dis_a = 138;
this.dis_b = 80;
} else {
this.dis_a = 97;
this.dis_b = 54;
}
if (1 == e.node.order) {
this.dis_a = 177;
this.dis_b = 102;
}
var b = Math.floor(Math.random() * this.newbridge.length);
3 == this.bridge_class && (b = 0 == this.thisbridge.out || 3 == this.thisbridge.out ? 1 : 0);
4 == this.bridge_class && (b = 0 == this.thisbridge.out || 3 == this.thisbridge.out ? 10 * Math.random() > 1 ? 1 : 3 : 10 * Math.random() > 1 ? 0 : 2);
5 == this.bridge_class && (b = 0 == this.random_step ? 2 : 0 == this.thisbridge.out || 3 == this.thisbridge.out ? 1 : 0);
this.prebridge = cc.instantiate(this.newbridge[b]);
this.prebridge.opacity = 0;
switch (e.node.out) {
case 0:
this.prebridge.setPosition(this.thisbridge.x + this.dis_a, this.thisbridge.y + this.dis_b);
g = -3;
break;

case 1:
this.prebridge.setPosition(this.thisbridge.x + this.dis_a, this.thisbridge.y - this.dis_b);
break;

case 2:
this.prebridge.setPosition(this.thisbridge.x - this.dis_a, this.thisbridge.y + this.dis_b);
g = -3;
break;

case 3:
this.prebridge.setPosition(this.thisbridge.x - this.dis_a, this.thisbridge.y - this.dis_b);
}
if (0 != this.random_step) {
this.prebridge.out = this.thisbridge.out;
this.random_step--;
} else {
0 == this.thisbridge.out || 3 == this.thisbridge.out ? this.prebridge.out = Math.floor(2 * Math.random()) + 1 : this.prebridge.out = 3 * Math.floor(2 * Math.random());
this.random_step = Math.floor(4 * Math.random()) + 1;
}
var p = 0;
if (this.random_position == this.random_group) {
switch (this.thisbridge.out - this.prebridge.out) {
case 0:
if (0 == this.thisbridge.out || 3 == this.prebridge.out) {
p = 0 + 2 * Math.floor(2 * Math.random());
var f = cc.instantiate(this.scene[p + 4 * this.bridge_class]);
} else {
p = 1 + 2 * Math.floor(2 * Math.random());
f = cc.instantiate(this.scene[p + 4 * this.bridge_class]);
}
break;

case 1:
this.random_position--;
break;

case 2:
p = 0 + 2 * Math.floor(2 * Math.random());
f = cc.instantiate(this.scene[p + 4 * this.bridge_class]);
break;

case -2:
p = 1 + 2 * Math.floor(2 * Math.random());
f = cc.instantiate(this.scene[p + 4 * this.bridge_class]);
break;

case -1:
if (2 == this.thisbridge) {
p = 0 + 2 * Math.floor(2 * Math.random());
f = cc.instantiate(this.scene[p + 4 * this.bridge_class]);
} else {
p = 1 + 2 * Math.floor(2 * Math.random());
f = cc.instantiate(this.scene[p + 4 * this.bridge_class]);
}
}
if (f) {
f.opacity = 0;
switch (e.node.out) {
case 0:
f.setPosition(this.thisbridge.x + 138, this.thisbridge.y + 80);
break;

case 1:
f.setPosition(this.thisbridge.x + 138, this.thisbridge.y - 80);
break;

case 2:
f.setPosition(this.thisbridge.x - 138, this.thisbridge.y + 80);
break;

case 3:
f.setPosition(this.thisbridge.x - 138, this.thisbridge.y - 80);
}
i.node.parent.addChild(f, this.thisbridge.zIndex + 3);
f.runAction(h);
setTimeout(function() {
f && f.runAction(l);
}.bind(this), 5e3);
}
}
i.node.parent.addChild(this.prebridge, this.thisbridge.zIndex + g);
this.prebridge.order = this.thisbridge.order + 1;
this.prebridge.runAction(r);
this.random_group--;
this.score++;
this.allbridge[this.score] = this.prebridge;
var m = this.score;
setTimeout(function() {
this.allbridge && this.allbridge[m].runAction(d);
}.bind(this), 5e3);
setTimeout(function() {
this.allbridge && this.allbridge[m].destroy();
}.bind(this), 6e3);
this.scorelabel.getComponent(cc.Label).string = "Score: " + this.score.toString();
}
},
onCollisionStay: function(e, i) {
this.isend = 0;
this.is_end = 0;
},
onCollisionExit: function(e, i) {
this.isend = 1;
},
share: function() {
qbuglyqq = qqbugly + "消息出错会在bugly报错 ";
},
submitScoreButtonFunc: function() {
var e = this.score;
CC_WECHATGAME ? window.wx.postMessage({
messageType: 3,
MAIN_MENU_NUM: "x1",
score: e
}) : cc.log("提交得分: x1 : " + e);
},
start: function() {},
update: function(e) {
this.is_end = this.is_end + this.isend;
if (2 == this.is_end) {
cc.audioEngine.stopAllEffects();
this.submitScoreButtonFunc();
cc.audioEngine.playEffect(this.endAudio, !1);
this.checkout.active = !0;
this.checkout.zIndex = 3e3;
this.scoreend.getComponent(cc.Label).string = this.score.toString();
this.node.parent.getComponent("maingame").acc = 0;
this.isend = 0;
this.is_end = 0;
}
Math.abs(this.node.x - this.nodex) + Math.abs(this.node.y - this.nodey) > 0 ? cc.audioEngine.resumeAll() : this.score > 11 && this.score < 50 && cc.audioEngine.pauseAll();
this.nodex = this.node.x;
this.nodey = this.node.y;
}
});
cc._RF.pop();
}, {} ]
}, {}, [ "RankingView", "gameagain", "gamebegin", "maingame", "playerjs" ]);