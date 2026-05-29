const BASE_URL= '/api'

export interface FileMetaData {
  name: string
  size: number
  lastModified: string
  contentType: string
}


export const getFiles = async (): Promise<FileMetaData[]> => {
  const response = await fetch(`${BASE_URL}/files`)
  return response.json()
}

export const uploadFile = async (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  await fetch(`${BASE_URL}/files/upload`, {
    method: 'POST',
    body: formData,
  })
}

export const downloadFile = async (fileName: string) => {
  const response = await fetch(`${BASE_URL}/files/download/${encodeURIComponent(fileName)}`)
  const blob = await response.blob()
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = fileName
  a.click()
  window.URL.revokeObjectURL(url)
}

export const deleteFile = async (fileName: string) => {
  await fetch(`${BASE_URL}/files/${encodeURIComponent(fileName)}`, {
    method: 'DELETE',
  })
}
