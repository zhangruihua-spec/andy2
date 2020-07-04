cc.Class({
    extends: cc.Component,

    properties: {
        rankview:cc.Node,
    },


    onLoad () {
        
    },
    game_home:function(){
        cc.director.loadScene("gamebegin");
        cc.audioEngine.stopAllEffects();
    },
    game_again:function(){
        cc.director.loadScene("rolling");
        cc.audioEngine.stopAllEffects();
    },
    
    paihangbang: function (){   
        cc.director.loadScene("ranking");
    },

    start () {
        
    },

    // update (dt) {},
});
