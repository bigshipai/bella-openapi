"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { register as apiRegister } from "@/lib/api/auth"
import { Button } from "@/components/common/button"
import { Input } from "@/components/common/input"
import { Label } from "@/components/common/label"
import { useToast } from "@/hooks/use-toast"

interface RegisterFormProps {
  redirect?: string
}

/**
 * 注册表单组件
 * 支持邮箱 + 密码 + 用户名（可选）注册
 */
export function RegisterForm({ redirect = '/overview' }: RegisterFormProps) {
  const [userName, setUserName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const router = useRouter()
  const { toast } = useToast()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!email.trim()) {
      toast({ title: '错误', description: '请输入邮箱', variant: 'destructive' })
      return
    }
    if (!password.trim()) {
      toast({ title: '错误', description: '请输入密码', variant: 'destructive' })
      return
    }
    if (password.length < 6) {
      toast({ title: '错误', description: '密码长度不能少于6位', variant: 'destructive' })
      return
    }
    if (password !== confirmPassword) {
      toast({ title: '错误', description: '两次密码输入不一致', variant: 'destructive' })
      return
    }

    setIsLoading(true)
    try {
      const result = await apiRegister(email, password, userName || undefined)

      // 注册成功，保存 token 并跳转
      if (typeof window !== 'undefined' && result.token) {
        localStorage.setItem('X-Auth-Token', result.token)
      }
      toast({ title: '注册成功', description: '欢迎加入 Bella OpenAPI' })
      router.push(redirect)
    } catch (error) {
      toast({
        title: '注册失败',
        description: error instanceof Error ? error.message : '邮箱可能已被注册',
        variant: 'destructive'
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="userName">用户名（可选）</Label>
        <Input
          id="userName"
          type="text"
          placeholder="给自己起个名字"
          value={userName}
          onChange={(e) => setUserName(e.target.value)}
          disabled={isLoading}
          autoComplete="name"
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="email">邮箱 *</Label>
        <Input
          id="email"
          type="email"
          placeholder="请输入邮箱地址"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          disabled={isLoading}
          autoComplete="email"
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="password">密码 *</Label>
        <Input
          id="password"
          type="password"
          placeholder="至少6位密码"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          disabled={isLoading}
          autoComplete="new-password"
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="confirmPassword">确认密码 *</Label>
        <Input
          id="confirmPassword"
          type="password"
          placeholder="再次输入密码"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          disabled={isLoading}
          autoComplete="new-password"
          required
        />
      </div>

      <Button type="submit" className="w-full" disabled={isLoading}>
        {isLoading ? '注册中...' : '注册'}
      </Button>
    </form>
  )
}
