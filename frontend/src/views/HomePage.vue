<template>
  <div class="dashboard-container">
    <Sidebar />
    <div class="main-content">
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Sidebar from '@/views/navigation/Sidebar.vue'

export default {
  components: {
    Sidebar
  }
}
</script>

<style scoped>
.dashboard-container {
  display: flex;
  height: 100vh;
}

.main-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  max-height: 100vh;
  background-color: var(--vt-c-black-mute);
  color: var(--vt-c-text-dark-2);
}

.content-area {
  padding: 20px;
  height: calc(100vh);
  overflow-y: auto;
}
</style>
