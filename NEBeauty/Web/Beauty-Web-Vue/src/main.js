import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/styles/common.less'

Vue.config.productionTip = false

Vue.use(ElementUI)

new Vue({
    router,
    data: function() {
        return {
            isStarted: true
        }
    },
    render: h => h(App)
}).$mount('#app')
