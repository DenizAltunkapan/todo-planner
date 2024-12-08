import { createRouter, createWebHistory } from 'vue-router'
import AssigneeView from '@/views/assignees/AssigneeView.vue'
import CreateAssigneeView from '@/views/assignees/CreateAssignee.vue'
import AssigneeDetailView from '@/views/assignees/AssigneeDetail.vue'
import ToDoListView from '@/views/todos/ToDoListView.vue'
import CreateToDoView from '@/views/todos/CreateToDoView.vue'
import ToDoDetailView from '@/views/todos/ToDoDetailView.vue'
import AboutView from '@/views/AboutView.vue'
// @ts-ignore
import HomePage from '@/views/HomePage.vue'

const routes = [
  {
    path: '/',
    redirect: '/todos' // Automatische Weiterleitung zu /todos
  },
  {
    path: '/',
    component: HomePage,
    children: [
      { path: 'assignees', component: AssigneeView },
      { path: 'create-assignee', component: CreateAssigneeView },
      { path: 'assignees/:id', component: AssigneeDetailView, props: true },
      { path: 'todos', component: ToDoListView },
      { path: 'create-todo', component: CreateToDoView },
      { path: 'todos/:id', component: ToDoDetailView, props: true },
      { path: 'about', component: AboutView }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
