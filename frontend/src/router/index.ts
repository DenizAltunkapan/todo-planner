import { createRouter, createWebHistory } from 'vue-router'
import AssigneeView from '@/views/assignees/AssigneesView.vue'
import CreateAssigneeView from '@/views/assignees/CreateAssignee.vue'
import AssigneeDetailView from '@/views/assignees/AssigneeDetail.vue'
import ToDoListView from '@/views/todos/ToDosView.vue'
import CreateToDoView from '@/views/todos/CreateToDo.vue'
import ToDoDetailView from '@/views/todos/ToDoDetail.vue'
// @ts-ignore
import HomePage from '@/views/HomePage.vue'

const routes = [
  {
    path: '/',
    redirect: '/todos'
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
      { path: 'todos/:id', component: ToDoDetailView, props: true }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
