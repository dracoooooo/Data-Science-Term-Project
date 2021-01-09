var app = new Vue({
    el: '#app',
    data: {
        message: 'Hello Vue!' + new Date().toLocaleString(),
        seen: true,
        todos:[
            {text: "test1"},
            {text: "test2"},
            {text: "test3"}
        ],
        styleMsg: {
            color: 'green',
            textShadow: '0 0 5px green'
        },
        rawHtml: "<p style='color: aqua'>Ho la</p>",
        number: 0,
        url: "https://blog.dracode.top",
        items:[{
            name: 'david',
            age: 30
        },{
            name: 'yahaha',
            age: 5
        },{
            name: 'link',
            age: 107
        }
        ]
    },
    methods:{
        click: function () {
            if(this.seen === true) this.seen = false;
            else this.seen = true;
        },
        change: function () {
            this.message = "text changed";
        },
        plusOne: function () {
            this.number += 1;
        }
    },
    //watch属性中的对象会在元素改变之后执行方法
    //watch属性会监听当前变量值的变化
    watch:{
        message: function(newlval, oldval){
            console.log("new val: " + newlval);
            console.log("old val: " + oldval);
        }
    },
    //会监听所有本实例(this或者是app)内的对msg1又影响的值的变化
    computed:{
        msg1: function () {
            return 'computed: ' + this.message + '; number: ' + this.number;
        }
    }

});
//
// while(true){
//     app.message =  new Date().toLocaleString();
// }


