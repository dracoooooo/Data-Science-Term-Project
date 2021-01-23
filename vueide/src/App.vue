<template>
<!--  <div id="app">-->
<!--    <img alt="Vue logo" src="./assets/logo.png">-->
<!--    <HelloWorld msg="Welcome to Your Vue.js App"/>-->
<!--  </div>-->
  <div>
    <ide></ide>
    <side-bar></side-bar>
  </div>
</template>

<script>

import ide from "@/components/ide";
import SideBar from "@/components/sideBar";

export default {
  name: 'App',
  components: {
    SideBar,
    ide
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
