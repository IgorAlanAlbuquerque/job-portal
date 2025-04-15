<template>
    <div class="login-page">
      <div class="login-container">
        <LoginForm />
      </div>
    </div>
  </template>
  
  <script setup>
  import LoginForm from '@/components/LoginForm.vue'
  </script>
  
  <style scoped lang="scss">
  .login-page {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background: linear-gradient(to right, #667eea, #764ba2);
  }
  </style>