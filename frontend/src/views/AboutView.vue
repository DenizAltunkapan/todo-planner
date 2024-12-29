<template>
  <div class="about">
    <h1>About the Developer</h1>
    <div class="student-info">
      <p><span class="label">Name:</span> {{ student.name }}</p>
      <p><span class="label">Matrikelnr.:</span> {{ student.id }}</p>
      <p><span class="label">Major:</span> {{ student.program }}</p>
      <p><span class="label">University:</span> {{ student.university }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const student = reactive({
  name: 'Deniz Altunkapan',
  id: '3721629',
  program: 'Software Engineering',
  university: 'University of Stuttgart'
})
</script>

<style scoped>
h1 {
  text-align: center;
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  color: #ffffff;
  padding-top: 15px;
}

.student-info p {
  font-size: 1.1rem;
  margin: 15px 0;
  line-height: 1.5;
  color: #b9b9b9;
}
</style>
