// Learn cc.Class:
//  - [Chinese] http://docs.cocos.com/creator/manual/zh/scripting/class.html
//  - [English] http://www.cocos2d-x.org/docs/creator/en/scripting/class.html
// Learn Attribute:
//  - [Chinese] http://docs.cocos.com/creator/manual/zh/scripting/reference/attributes.html
//  - [English] http://www.cocos2d-x.org/docs/creator/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - [Chinese] http://docs.cocos.com/creator/manual/zh/scripting/life-cycle-callbacks.html
//  - [English] http://www.cocos2d-x.org/docs/creator/en/scripting/life-cycle-callbacks.html

cc.Class({
    extends: cc.Component,

    properties: {
       roll:[cc.Node],
       player:cc.Node,
       light:cc.Node,
       light0:cc.Node,
    },

    // LIFE-CYCLE CALLBACKS:

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
        var moveAction1 = cc.moveTo(3, -158,this.roll[0].y);
        var moveAction2 = cc.moveTo(3, 0,this.roll[0].y);
        var moveAction3 = cc.moveTo(3, 158,this.roll[0].y);
        var rollAction1 = cc.rotateBy(3,720);
        var rollAction2 = cc.rotateBy(3,720);
        var rollAction3 = cc.rotateBy(3,720);
        var moveAction4 = cc.moveBy(4,cc.view.getVisibleSize().width+200,0);
        var repeat = cc.repeatForever(moveAction4);
        this.roll[0].runAction(moveAction1);
        this.roll[1].runAction(moveAction2);
        this.roll[2].runAction(moveAction3);
        this.roll[0].runAction(rollAction1);
        this.roll[1].runAction(rollAction2);
        this.roll[2].runAction(rollAction3);
        this.player.runAction(repeat);
        var seq1 = cc.repeatForever(cc.sequence(cc.fadeIn(1), cc.fadeOut(1), cc.rotateBy(2,360)));
        var seq2 = cc.repeatForever(cc.sequence(cc.rotateBy(2,360),cc.fadeIn(1), cc.fadeOut(1)));
        this.light.runAction(seq1);
        this.light0.runAction(seq2);
    },
    game_again:function(){
        cc.director.loadScene("rolling");
    },
    paihangbang: function (){   
        // let score = 111;
        // if (CC_WECHATGAME) {
        //     window.wx.postMessage({
        //         messageType: 3,
        //         MAIN_MENU_NUM: "x1",
        //         score: score,
        //     });
        // } else {
        //     cc.log("提交得分: x1 : " + score)
        // }
        cc.director.loadScene("ranking");
    },
    share: function (){   
        if (CC_WECHATGAME) {
            // //主动拉起分享接口
            cc.loader.loadRes("texture/share",function(err,data){
                wx.shareAppMessage({
                    title: "我发现了一个很好玩的小游戏，赶紧一起来滚吧~",
                    imageUrl: data.url,
                    success(res){
                        console.log("转发成功!!!")
                    },
                    fail(res){
                        console.log("转发失败!!!")
                    } 
                })
            });
        }
    },
    start () {
        
    },

     update (dt) {
        //console.log(this.player.x);
         if(this.player.x > 500){
            this.player.setPosition(-cc.view.getVisibleSize().width/2,this.player.y);
            var rollAction1 = cc.rotateBy(2,360);
            var rollAction2 = cc.rotateBy(2,360);
            var rollAction3 = cc.rotateBy(2,360);
            this.roll[0].runAction(rollAction1);
            this.roll[1].runAction(rollAction2);
            this.roll[2].runAction(rollAction3);
         }
     },
});
