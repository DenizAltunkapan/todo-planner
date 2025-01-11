<template>
  <div class="sidebar">
    <div class="content">
      <div class="menu">
        <router-link to="/todos" class="menu-item">
          <span>ToDos</span>
        </router-link>
        <router-link to="/create-todo" class="menu-item">
          <span>Create ToDo</span>
        </router-link>
        <router-link to="/assignees" class="menu-item">
          <span>Assignees</span>
        </router-link>
        <router-link to="/create-assignee" class="menu-item">
          <span>Create Assignee</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.sidebar {
  width: 13%;
  height: 100vh;
  background-color: var(--vt-c-black-mute);
  transition: 300ms;
}

.content {
  padding-top: 15px;
}

.menu {
  text-align: left;
  padding: 0 0.5rem;
}

span {
  font-weight: bolder;
  font-size: 15px;
}
.menu-item {
  display: block;
  padding: 0.7rem;
  color: white;
  text-decoration: none;
}

.menu-item:hover {
  color: #007bff;
}
</style>
