<template>
  <div>
<!--    <codemirror></codemirror>-->
    <textarea v-model="code"></textarea>
    <div>
    <button  @click="getLangAndFunc" class="btn btn-primary">Predict</button>
      <p>lang: {{lang}}</p>
      <p>lang prob: {{lang_prob}}</p>
      <p>func: {{func}}</p>
      <p>func prob: {{func_prob}}</p>
    </div>
    <div>
      <button  @click="getRecommendCode" class="btn btn-primary">Recommend</button>
      <h1>recommend code:</h1>
      <li v-for="r in Recommends">
      <p>{{r.name[0]}}</p>
      <textarea class="text-wrapper">{{r.code[0]}}</textarea>
      <p>{{r.similarity}}</p>
      <p>{{r.quality}}</p>
    </li>
    </div>
  </div>
</template>

<script>
import codemirror from "@/assets/vue-codemirror-master/vue-codemirror-master/src/codemirror";
import axios from 'axios'
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

      cmOption: {
        tabSize: 4,
        styleActiveLine: true,
        lineNumbers: true,
        line: true,
        mode: 'text/javascript',
        lineWrapping: true,
        theme: 'default'
      }
    }
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
            this.Recommends.push(res.data['r0']);
            this.Recommends.push(res.data['r1']);
            this.Recommends.push(res.data['r2']);
            // this.Recommends[0].code[0] = this.Recommends[0].code[0].replace(/\n/g, "<br/>")
            // this.Recommends[1].code[0] = this.Recommends[1].code[0].replace(/\n/g, "<br/>")
            // this.Recommends[2].code[0] = this.Recommends[2].code[0].replace(/\n/g, "<br/>")
          })
    }
  },
  component: {
  }

}
</script>

<style>
.text-wrapper {
  white-space: pre-wrap;
}
</style>
