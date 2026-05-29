<template>
  <div>
    <h2>Fichiers du bucket</h2>

    <input type="file" @change="onFileSelected" />
    <button @click="onUpload">Uploader</button>

    <table>
      <thead>
        <tr>
          <th>Nom</th>
          <th>Taille</th>
          <th>Date de modification</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="file in files" :key="file.name">
          <td>{{ file.name }}</td>
          <td>{{ (file.size / 1024).toFixed(2) }} Ko</td>
          <td>{{ new Date(file.lastModified).toLocaleString('fr-FR') }}</td>
          <td>
            <button @click="onDownload(file.name)">Télécharger</button>
            <button @click="onDelete(file.name)">Supprimer</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getFiles, uploadFile, downloadFile, deleteFile, type FileMetaData } from '@/api/s3'

const files = ref<FileMetaData[]>([])
const selectedFile = ref<File | undefined>(undefined)

const loadFiles = async () => {
  files.value = await getFiles()
}

const onFileSelected = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files.length > 0) selectedFile.value = input.files[0]
}

const onUpload = async () => {
  if (!selectedFile.value) return
  await uploadFile(selectedFile.value!)
  await loadFiles()
}

const onDownload = async (fileName: string) => {
  await downloadFile(fileName)
}

const onDelete = async (fileName: string) => {
  await deleteFile(fileName)
  await loadFiles()
}

onMounted(loadFiles)
</script>
