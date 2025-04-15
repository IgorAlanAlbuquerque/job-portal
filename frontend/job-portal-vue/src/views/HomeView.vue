<template>
    <div class="background">
      <nav class="main_div">
        <div class="logo_left">
          <label class="logo">hotdevjobs</label>
          <label class="logo_com">.com</label>
        </div>
        <div class="logo_right">
          <label class="Candidate_login_btn">
            <RouterLink to="/login">Job Candidate Login</RouterLink>
            <RouterLink to="/register">/Register</RouterLink>
          </label>
          <label class="Recruiter_login_btn">
            <RouterLink to="/login">Recruiter Login</RouterLink>
            <RouterLink to="/register">/Register</RouterLink>
          </label>
        </div>
      </nav>
  
      <form @submit.prevent="submitSearch">
        <div class="searchBox">
          <div class="inner">
            <input type="text" placeholder="Search for a job" v-model="job" />
            <input type="text" placeholder="Enter a location" v-model="location" />
            <button type="submit"><i class="fa fa-search"></i></button>
          </div>
        </div>
      </form>
  
      <footer>
        <label class="copy-right">Copyright © hotdevjobs.com</label>
      </footer>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'
  
  const job = ref('')
  const location = ref('')
  const router = useRouter()
  
  const submitSearch = () => {
    router.push({
      path: '/global-search/',
      query: { job: job.value, location: location.value }
    })
  }
  </script>
  
  <style scoped lang="scss">
  @import "@/assets/styles.scss"; // ou importe o equivalente
  
  .background {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }
  
  .main_div {
    display: flex;
    justify-content: space-between;
    padding: 1rem;
    background-color: #fff;
  
    .logo {
      font-size: 1.5rem;
      font-weight: bold;
    }
  
    .logo_com {
      color: #888;
    }
  
    .logo_right label {
      margin-left: 1rem;
  
      a {
        margin-right: 0.5rem;
      }
    }
  }
  
  .searchBox {
    display: flex;
    justify-content: center;
    margin-top: 2rem;
  
    .inner {
      display: flex;
      gap: 0.5rem;
  
      input {
        padding: 0.5rem;
      }
  
      button {
        background-color: #333;
        color: white;
        padding: 0.5rem 1rem;
      }
    }
  }
  
  footer {
    text-align: center;
    padding: 2rem;
    margin-top: auto;
  }
  </style>