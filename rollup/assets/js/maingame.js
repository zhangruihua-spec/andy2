cc.Class({
    extends: cc.Component,

    properties: {
        speed: 0,
        player:cc.Node,
        target: cc.Node,
        tips:cc.Node,
        colorPrefab: cc.Prefab,
        _time: 0,
        _range: cc.v2(0, 0),
        _acc: cc.v2(0, 0),
        xSpeed:0,
        ySpeed:0,
        acc:1,
    },

    onLoad () {
        if (CC_WECHATGAME) {
            //开启右上角的分享
             wx.showShareMenu();
            //监听右上角的分享调用 
            cc.loader.loadRes("texture/share",function(err,data){
                wx.onShareAppMessage(function(res){
                    return {
                        title: "我发现了一个很好玩的小游戏，赶紧一起来滚吧~",
                        imageUrl: data.url,
                        success(res){
                            console.log("转发成功!!!")
                            common.diamond += 20;
                        },
                        fail(res){
                            console.log("转发失败!!!")
                        } 
                    }
                })
            }); 
        }
        
        this.target.zIndex =2000;
        
        this.accUp = false;
        this.accDown = false;
        this.accLeft = false;
        this.accRight = false;
        //开启碰撞
        // cc.director.getCollisionManager().enabled = true;
        // cc.director.getCollisionManager().enabledDebugDraw = true;
        
        var screenSize = cc.view.getVisibleSize();
        this._range.x = screenSize.width / 2 - this.target.width / 2;
        this._range.y = screenSize.height / 2 - this.target.height / 2;
        cc.systemEvent.setAccelerometerEnabled(true);
        cc.systemEvent.on(cc.SystemEvent.EventType.DEVICEMOTION, this.onDeviceMotionEvent, this);     
        cc.systemEvent.on(cc.SystemEvent.EventType.KEY_DOWN, this.onKeyDown, this);
        cc.systemEvent.on(cc.SystemEvent.EventType.KEY_UP, this.onKeyUp, this);
 
        var actionOut = cc.fadeOut(1);
        this.acc = 0;
        setTimeout(function () { 
            this.tips.runAction(actionOut);
          }.bind(this), 1000);
        setTimeout(function () { 
            this.player.active = "true";
            this.acc = 1;
          }.bind(this), 2000);
    
    },

    onDestroy () {
        cc.systemEvent.setAccelerometerEnabled(false);
        cc.systemEvent.off(cc.SystemEvent.EventType.DEVICEMOTION, this.onDeviceMotionEvent, this);
        cc.systemEvent.off(cc.SystemEvent.EventType.KEY_DOWN, this.onKeyDown, this);
        cc.systemEvent.off(cc.SystemEvent.EventType.KEY_UP, this.onKeyUp, this);
    },
    
    onKeyDown: function (event) {
        switch(event.keyCode) {
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
                break;
        }
    },
 
    onKeyUp: function (event) {
        switch(event.keyCode) {
            case cc.macro.KEY.w:
                this.ySpeed = 0;
                break;
            case cc.macro.KEY.s:
                this.ySpeed = 0;
                break;
            case cc.macro.KEY.a:
                this.xSpeed = 0;
                break;
            case cc.macro.KEY.d:
                this.xSpeed = 0;
                break;
        }
    },
    onDeviceMotionEvent (event) {
        // if(Math.abs(this._acc.x-event.acc.x)>Math.abs(event.acc.x) || Math.abs(this._acc.y-event.acc.y)>Math.abs(event.acc.y)){
        //     this._time -= 1;
        // }
        // else{
        //     this._time += 1;
        // }
        var u = navigator.userAgent;
        var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
        if(isiOS){
            this._acc.x =  -event.acc.x*2;
            this._acc.y =  -event.acc.y*2;
        }
        else{
            this._acc.x =  event.acc.x*2;
            this._acc.y =  event.acc.y*2;
        }
        
    },
    
    update (dt) {
        // 根据当前速度更新主角的位置
        //键盘控制
        this.target.x += dt * this.xSpeed * this.acc;
        this.target.y += dt * this.ySpeed * this.acc;
    
        //重力控制
        this.target.x += this._acc.x * dt * (this.speed+this._time) * this.acc;
        this.target.y += this._acc.y * dt * (this.speed+this._time) * this.acc;

        //限制屏幕范围
        // this.target.x = cc.misc.clampf(this.target.x, -range.x, range.x);
        // if (this.target.x <= -range.x || this.target.x >= range.x) {
        //     this._time = 0;
        // }  

        //镜头跟随
        this.node.setPosition(-this.target.x,-this.target.y);
    },
    

});

