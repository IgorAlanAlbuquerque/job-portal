<template>
    <form class="login-form" @submit.prevent="handleLogin">
      <h2>Login</h2>
      <div class="form-group">
        <label for="username">Usuário</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div class="form-group">
        <label for="password">Senha</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button type="submit">Entrar</button>
      <p class="register-link">
        Não tem uma conta? <a href="/register">Cadastre-se</a>
      </p>
    </form>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  
  const username = ref('')
  const password = ref('')
  
  const handleLogin = () => {
    // Aqui você pode chamar a API de autenticação
    console.log(`Usuário: ${username.value}, Senha: ${password.value}`)
  }
  </script>
  
  <style scoped lang="scss">
  @import "@/assets/styles/login.scss";
  </style>