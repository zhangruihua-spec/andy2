cc.Class({
    extends: cc.Component,

    properties: {
        bg:cc.Node,
        bridge:[],  //定义一个桥的数组存放各种桥
        allbridge:[],   //定义一个桥的数组存放所有生成的桥
        colorPrefab:[cc.Prefab],    //色块  无效果  音符声
        beachPrefab:[cc.Prefab],    //沙滩  变慢    沙滩声
        gardenPrefab:[cc.Prefab],   //草地  树木    草地声  鸟叫声
        bridgePrefab: [cc.Prefab],  //木桥  变窄    木板声        
        brickPrefab:[cc.Prefab],    //砖块  坑地    石块声  
        roadPrefab:[cc.Prefab],     //马路  加快    汽笛声  
        scene:[cc.Prefab],         //场景对应小件
        
                
        startbridge:cc.Node,        //起始点
        isend:0,                //判断游戏是否结束
        is_end:0,
        lastbridge:cc.Node,         //上一个
        thisbridge:cc.Node,         //当前
        prebridge:cc.Node,          //下一个

        score:0,                    //计分
        checkout:cc.Node,           //结束蒙版
        scorelabel:cc.Node,         //计分器
        scoreend:cc.Node,           //结束得分

        timeout:0,                  //定义一个延时变量
        lastorder:0,                //上一个桥的序号
        thisorder:0,                //当前桥的序号
        random_step:0,              //随机生成 转向 步数
        random_group:0,             //随机生成 分组 个数
        random_position:0,          //随机生成 分组 位置
        tipstring_num:0,            //定义页面下方字符的个数
        bridge_class:0,             //判断桥的种类
        last_bridge_class:0,        //上一个桥的种类
        newbridge:[],               //定义一个桥的数组存放即将生成的桥

        dis_a:0,                    //两桥之间水平位移
        dis_b:0,                    //两桥之间垂直位移

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
        nodex:0,
        nodey:0,                //判断是否移动中
        tipslabel:cc.Node,      //提示标语
        tipslabel1:cc.Node,      //提示标语
    },

    onLoad () { 
        //开启碰撞
        cc.director.getCollisionManager().enabled = true;
        //cc.director.getCollisionManager().enabledDebugDraw = true;
        //初始化随机步数                                                           随机生成【0,1】：Math.floor(Math.random()*2)；
        this.random_step = Math.floor(Math.random()*4)+1;
        //初始化桥的数组
        this.bridge = [this.colorPrefab,this.beachPrefab,this.gardenPrefab,this.bridgePrefab,this.brickPrefab,this.roadPrefab];
        //初始化起始点的序号
        this.startbridge.order = 1;
        this.startbridge.out = 3;
        this.thisbridge = this.startbridge;
        //初始化水平垂直位移
        this.dis_a = 138;
        this.dis_b = 80;
        //初始化运动状态
        this.nodex = 0;
        this.nodey = 0;
        this.random_group =Math.floor(Math.random()*5)+5;
        this.random_position =Math.floor(Math.random()*(this.random_group-3))+2;
        this.newbridge = this.bridge[0];
        this.score = -1;
        this.allbridge = [];
/***************************ceshi */
        // var newscene = cc.instantiate(this.scene[6]);
        // newscene.setPosition(this.thisbridge.x-177,this.thisbridge.y-102);
        // this.node.parent.addChild(newscene,this.thisbridge.zIndex+3);
    },
    
    onCollisionEnter: function (other, self) {
        var colorAction = cc.tintTo(2, 255, 254, 203);
        var beachAction = cc.tintTo(2, 204, 204, 204);
        var gardenActon = cc.tintTo(2, 204, 255, 154);
        var bridgeAction = cc.tintTo(2, 254, 204, 203);
        var brickAction = cc.tintTo(2, 204, 255, 255);
        var roadAction = cc.tintTo(2, 204, 204, 254);
        
        //定义运动状态
        // var actionTo = cc.moveBy(2, cc.v2(0,40));
        // var actionDown = cc.moveBy(0.2, cc.v2(0,-40));
        var actionIn = cc.fadeIn(1);
        var actionIn1 = cc.fadeIn(1);
        var actionOut = cc.fadeOut(1);
        var actionOut1 = cc.fadeOut(1);

        //给桥的序号赋值
        if(this.lastbridge){
            this.lastorder = this.lastbridge.order;
        }
        if(this.thisbridge){            
            this.thisorder = this.thisbridge.order;
        }
        
        //给不同的桥定义不同的玩法
        switch(other.node.name){
            case "color0":
                cc.audioEngine.stopAllEffects();
                cc.audioEngine.playEffect(this.colorAudio[0], false);
                this.node.parent.getComponent("maingame").speed = 300;
                break;
            case "color1":
                cc.audioEngine.stopAllEffects();
                cc.audioEngine.playEffect(this.colorAudio[1], false);
                this.node.parent.getComponent("maingame").speed = 300; 
                break;
            case "color2":
                cc.audioEngine.stopAllEffects();
                cc.audioEngine.playEffect(this.colorAudio[2], false);
                this.node.parent.getComponent("maingame").speed = 300; 
                break;
            case "color3":
                cc.audioEngine.stopAllEffects();
                cc.audioEngine.playEffect(this.colorAudio[3], false);
                this.node.parent.getComponent("maingame").speed = 300; 
                break;
            case "color4":
                cc.audioEngine.stopAllEffects();
                cc.audioEngine.playEffect(this.colorAudio[4], false);
                this.node.parent.getComponent("maingame").speed = 300; 
                break;
            case "color5":
                cc.audioEngine.stopAllEffects();
                cc.audioEngine.playEffect(this.colorAudio[5], false);
                this.node.parent.getComponent("maingame").speed = 300; 
                break;     
            case "beach0":
                this.node.parent.getComponent("maingame").speed = 200;
                break;
            case "garden0":
                this.node.parent.getComponent("maingame").speed = 300;                              
                break;    
            case "bridge0":
                this.node.parent.getComponent("maingame").speed = 300;                              
                break;  
            case "bridge1":
                this.node.parent.getComponent("maingame").speed = 300;                              
                break;  
            case "brick0":
                this.node.parent.getComponent("maingame").speed = 300;                              
                break;     
            case "brick1":
                this.node.parent.getComponent("maingame").speed = 300;                              
                break;       
            case "road0":
                this.node.parent.getComponent("maingame").speed = 450;
                break;  
            case "road1":
                this.node.parent.getComponent("maingame").speed = 450;
                break;  
            case "road2":
                this.node.parent.getComponent("maingame").speed = 450;
                break;       
        }   
        var tipstring = ["重力控制小球滚动","你给我有多远滚多远","你让我滚，我滚了","你让我回来，对不起，滚远了","思想有多远，你给我滚多远","光速有多快，你给我滚多快"];
        if(this.tipstring_num == 6){
            this.tipstring_num = 0;
        }
        if(this.bridge_class==0){
            this.bg.runAction(colorAction);
            //cc.audioEngine.stopAllEffects();
        }     
        if(this.bridge_class==1 && this.last_bridge_class != this.bridge_class){
            this.bg.runAction(beachAction);
            cc.audioEngine.stopAllEffects();
            cc.audioEngine.playEffect(this.beachAudio, true);
        }
        if(this.bridge_class==2 && this.last_bridge_class != this.bridge_class){
            this.bg.runAction(gardenActon);
            cc.audioEngine.stopAllEffects();
            cc.audioEngine.playEffect(this.gardenAudio, true);
        }
        if(this.bridge_class==3 && this.last_bridge_class != this.bridge_class){
            this.bg.runAction(bridgeAction);
            cc.audioEngine.stopAllEffects();
            cc.audioEngine.playEffect(this.bridgeAudio, true);
        }
        if(this.bridge_class==4 && this.last_bridge_class != this.bridge_class){
            this.bg.runAction(brickAction);
            cc.audioEngine.stopAllEffects();
            cc.audioEngine.playEffect(this.brickAudio, true);
        }
        if(this.bridge_class==5 && this.last_bridge_class != this.bridge_class){
            this.bg.runAction(roadAction);
            cc.audioEngine.stopAllEffects();
            cc.audioEngine.playEffect(this.carAudio, true);
        }
        //碰撞事件
        if(other.node.order-2 == this.score){
            //登上第二座桥时起点消失
            if(other.node.order == 4){
                this.startbridge.runAction(actionOut);
            }
            //last消失      
            // last=this     
            // this=other    
            // if(this.lastbridge){   //上一座桥消失
            //     this.lastbridge.runAction(actionDown);
            //     //this.lastbridge.runAction(actionOut);
            //     this.lastbridge.destroy();
            // }
            this.lastbridge = this.thisbridge;     
            this.thisbridge = other.node; 
            //定义层级，判断是否遮挡
            var indexn = 1;
            
              
            //给不同宽窄的桥赋值不同的位移
            var isbridge = false;
            if(this.bridge_class == 3){
                isbridge = true;
            }  
            else{
                isbridge = false;
            }
            //产生碰撞时生成随机数（10-20个）
            this.last_bridge_class = this.bridge_class;
            if(this.random_group == 0){                            
                this.bridge_class = Math.floor(Math.random()*6);                
                this.newbridge = this.bridge[this.bridge_class];
                this.tipslabel.getComponent(cc.Label).string = tipstring[this.tipstring_num];
                this.tipstring_num ++;
                this.random_group = Math.floor(Math.random()*5)+5;
                this.random_position =Math.floor(Math.random()*(this.random_group-3))+2;
            }
            
            if(isbridge && this.bridge_class ==3){
                this.dis_a = 63;
                this.dis_b = 38;
            }
            else if(isbridge && this.bridge_class !=3){
                this.dis_a = 97;
                this.dis_b = 54;
            }
            else if(!isbridge && this.bridge_class ==3){
                this.dis_a = 97;
                this.dis_b = 54;
            }
            else{
                this.dis_a = 138;
                this.dis_b = 80;
            }
            if(other.node.order == 1){
                this.dis_a = 177;
                this.dis_b = 102;
            }
            //随机生成一个桥
            var numberic = Math.floor(Math.random()*this.newbridge.length) ;

            //如果是brige 判断是哪一边
            if(this.bridge_class == 3){
                if(this.thisbridge.out == 0 || this.thisbridge.out == 3) {
                    numberic = 1;
                }
                else{
                    numberic = 0;
                }
            }
            //如果是brick
            if(this.bridge_class == 4){
                if(this.thisbridge.out == 0 || this.thisbridge.out == 3) {
                    if(Math.random()*10>1)
                        numberic = 1;
                    else
                        numberic = 3;
                }
                else{
                    if(Math.random()*10>1)
                        numberic = 0;
                    else
                        numberic = 2;
                }
            }
            //如果是road
            if(this.bridge_class == 5){
                if(this.random_step == 0){
                    numberic = 2;
                }
                else{
                    if(this.thisbridge.out == 0 || this.thisbridge.out == 3) {
                        numberic = 1;
                    }
                    else{
                        numberic = 0;
                    }
                }
            }
            //生成新桥  0132顺时针
            this.prebridge = cc.instantiate(this.newbridge[numberic]);
            this.prebridge.opacity = 0;

            switch(other.node.out){
                case 0:
                    this.prebridge.setPosition(this.thisbridge.x+this.dis_a,this.thisbridge.y+this.dis_b);
                    indexn = -3;
                    break;
                case 1:
                    this.prebridge.setPosition(this.thisbridge.x+this.dis_a,this.thisbridge.y-this.dis_b);
                    break;
                case 2:
                    this.prebridge.setPosition(this.thisbridge.x-this.dis_a,this.thisbridge.y+this.dis_b);
                    indexn = -3;
                    break;
                case 3:
                    this.prebridge.setPosition(this.thisbridge.x-this.dis_a,this.thisbridge.y-this.dis_b);
                    break;
            }
            if(this.random_step != 0){  //直向
                this.prebridge.out = this.thisbridge.out;
                this.random_step --;
            }
            else{                       //转向
                if(this.thisbridge.out == 0 || this.thisbridge.out == 3){
                    this.prebridge.out = Math.floor(Math.random()*2)+1;
                }
                else{
                    this.prebridge.out = Math.floor(Math.random()*2)*3;
                }
                
                this.random_step = Math.floor(Math.random()*4)+1;
            }

            /************************生成小件***********************/
            var scene_num = 0;
            if(this.random_position == this.random_group){
                switch(this.thisbridge.out-this.prebridge.out){
                    case 0:
                        if(this.thisbridge.out == 0 || this.prebridge.out == 3){
                            scene_num = 0+2* Math.floor(Math.random()*2)//左  随机数代表随机种类，两个小件中选一个
                            var newscene = cc.instantiate(this.scene[scene_num+this.bridge_class*4]); 
                        }
                        else{
                            scene_num = 1+2* Math.floor(Math.random()*2)//右
                            var newscene = cc.instantiate(this.scene[scene_num+this.bridge_class*4]); 
                        }
                        break;
                    case 1:   //没有
                        this.random_position--;
                        break;
                    case 2:
                        scene_num = 0+2* Math.floor(Math.random()*2)//左
                        var newscene = cc.instantiate(this.scene[scene_num+this.bridge_class*4]); 
                        break;
                    case -2:
                        scene_num = 1+2* Math.floor(Math.random()*2)//右
                        var newscene = cc.instantiate(this.scene[scene_num+this.bridge_class*4]); 
                        break;
                    case -1:
                        if(this.thisbridge == 2){
                            scene_num = 0+2* Math.floor(Math.random()*2)//左
                            var newscene = cc.instantiate(this.scene[scene_num+this.bridge_class*4]); 
                        }
                        else{
                            scene_num = 1+2* Math.floor(Math.random()*2)//右
                            var newscene = cc.instantiate(this.scene[scene_num+this.bridge_class*4]); 
                        }
                        break;
                }    
                if(newscene){
                    newscene.opacity = 0;   
                    switch(other.node.out){
                        case 0:
                            newscene.setPosition(this.thisbridge.x+138,this.thisbridge.y+80);
                            break;
                        case 1:
                            newscene.setPosition(this.thisbridge.x+138,this.thisbridge.y-80);
                            break;
                        case 2:
                            newscene.setPosition(this.thisbridge.x-138,this.thisbridge.y+80);
                            break;
                        case 3:
                            newscene.setPosition(this.thisbridge.x-138,this.thisbridge.y-80);
                            break;
                    }
                    self.node.parent.addChild(newscene,this.thisbridge.zIndex+3);
                    newscene.runAction(actionIn1);
                    setTimeout(function () {
                        if(newscene)
                        newscene.runAction(actionOut1);
                      }.bind(this), 5000);
                }
                
                  
            }            
            /************************生成小件end***********************/

            self.node.parent.addChild(this.prebridge,this.thisbridge.zIndex+indexn);
            this.prebridge.order = this.thisbridge.order+1;
            //this.prebridge.runAction(actionTo);
            this.prebridge.runAction(actionIn);
            
            this.random_group--;
            this.score++;
            this.allbridge[this.score] = this.prebridge;
            var n = this.score;
            setTimeout(function () {
                if(this.allbridge)
                this.allbridge[n].runAction(actionOut);
              }.bind(this), 5000);
            setTimeout(function () {
                if(this.allbridge)
                this.allbridge[n].destroy();
              }.bind(this), 6000);
            this.scorelabel.getComponent(cc.Label).string =  'Score: ' + this.score.toString();
        }        
    }, 
    onCollisionStay: function (other, self) {
        this.isend = 0;
        this.is_end = 0;
    },
    onCollisionExit: function (other, self) {
        this.isend = 1;
    },
    share: function (){   
        // function replacer (key, value) {
        //     if (typeof value === "string") {
        //       return undefined;
        //     }
        //     return value;
        //   }
          qbuglyqq=qqbugly + "消息出错会在bugly报错 "
        //   var foo = {foundation: "Mozilla", model: "box", week: 45, transport: "car", month: 7};
        //   var jsonString = JSON.stringify(foo, replacer);
        //   //jsb.reflection.callStaticMethod("org/cocos2dx/javascript/AppActivity", "setSysClipboardText", "(Ljava/lang/String;)V", ""); 
      
        // var person = [];
        // person[0] = "John";
        // person[1] = "Doe";
        // person[2] = 46;
        // var data = person[14];

        // buglyLog(1, "rollzrh", "878")
        // jsb.reflection.callStaticMethod("org/cocos2dx/javascript/AppActivity", "showAlertDialog", "(Ljava/lang/String;Ljava/lang/String;)V", "title",jsonString);
        
        
        
        
        
    },
    submitScoreButtonFunc(){
        let score = this.score;
        if (CC_WECHATGAME) {
            window.wx.postMessage({
                messageType: 3,
                MAIN_MENU_NUM: "x1",
                score: score,
            });
        } else {
            cc.log("提交得分: x1 : " + score)
        }
    },
    start () {

    },

    update (dt) {
        this.is_end = this.is_end + this.isend;
        if(this.is_end == 2){
            cc.audioEngine.stopAllEffects();
            this.submitScoreButtonFunc();
                cc.audioEngine.playEffect(this.endAudio, false);
                this.checkout.active = true;
                this.checkout.zIndex = 3000;
                this.scoreend.getComponent(cc.Label).string = this.score.toString();
                this.node.parent.getComponent("maingame").acc = 0; 
                this.isend = 0;
                this.is_end = 0;
        }
        //判断是否运动中
        if(Math.abs(this.node.x - this.nodex) + Math.abs(this.node.y - this.nodey) > 0){
            cc.audioEngine.resumeAll();
        }
        else{
            if(this.score>11 && this.score<50){
                cc.audioEngine.pauseAll();
            }
        };
        this.nodex = this.node.x;
        this.nodey = this.node.y;        
    },    
});
