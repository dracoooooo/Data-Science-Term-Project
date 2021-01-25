import Vue from 'vue'
import VueRouter from 'vue-router'
import test from "@/components/test";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: test
  }
]

const router = new VueRouter({
  routes
})

export default router
