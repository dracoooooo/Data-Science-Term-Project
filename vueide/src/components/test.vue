<template>
  <div class="codemirrorwrapper">
    <div>
    <codemirror v-model="code" id="codearea"></codemirror>
    </div>
      <div>
    <button @click="getLangAndFunc" class="btn btn-primary">Predict</button>
      <p>lang: {{lang}}</p>
      <p>lang prob: {{lang_prob}}</p>
      <p>func: {{func}}</p>
      <p>func prob: {{func_prob}}</p>
    </div>
    <div>
      <button  @click="getRecommendCode" class="btn btn-primary">Recommend</button>
      <h1 v-if="judge">recommend code:</h1>
    <div v-if="judge" v-for="r in Recommends">
      <p>{{r.name[0]}}</p>
      <p>sililarity: {{r.similarity[0]}}</p>
      <p>quality score: {{r.quality}}</p>
      <codemirror v-model="r.code[0]" class="text-wrapper"></codemirror>
      <hr>
    </div>
    <div v-else>
      <h1>not support this language</h1>
    </div>
    </div>

  </div>
</template>

<script>
import axios from 'axios'
import { codemirror } from 'vue-codemirror-lite'
export default {
  name: "test",
  data(){
    return {
      i: 0,
      code: '',
      lang: '',
      lang_prob: '',
      func: '',
      func_prob: '',
      Recommends: [],
      judge: false,

    }
  },
  mounted() {
    // use editor object...
    this.editor.focus()
    console.log('this is current editor object', this.editor)
  },
  methods:{
    getLangAndFunc(){
      const path = 'http://127.0.0.1:5000/LandF/';
      axios.post(path, {'code': this.code})
        .then((res)=>{
          console.log(res)
          this.lang = res.data['lang'];
          this.func = res.data['func'];
          this.lang_prob = res.data['lang_prob'];
          this.func_prob = res.data['func_prob'];
        })
    },
    getRecommendCode(){
      this.Recommends = [];
      const path = 'http://127.0.0.1:5000/getcode/'
      axios.post(path, {'code': this.code})
          .then((res)=>{
            console.log(res)
            if (res.data === "not support this language"){
              this.judge = false;
            }
            else {
              this.judge = true;
              this.Recommends.push(res.data['r0']);
              this.Recommends.push(res.data['r1']);
              this.Recommends.push(res.data['r2']);
            }
            // this.Recommends[0].code[0] = this.Recommends[0].code[0].replace(/\n/g, "<br/>")
            // this.Recommends[1].code[0] = this.Recommends[1].code[0].replace(/\n/g, "<br/>")
            // this.Recommends[2].code[0] = this.Recommends[2].code[0].replace(/\n/g, "<br/>")
          })
    }
  },
  components: {
    codemirror
  }


}
</script>

<style>
.text-wrapper {
  white-space: pre-wrap;
  width: 70%;
  height: 300px;
  text-align: left;
  left: 15%;
  right: 15%;
  position: relative;
}

#codearea{

  height: 300px;
  text-align: left;
  left: 15%;
  right: 15%;
  position: relative;
}

.codemirrorwrapper{
  left: 30%;
}

body{
  text-align: center;
}

</style>
