"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/components/providers/auth-provider"
import { Button } from "@/components/common/button"
import { Input } from "@/components/common/input"
import { Label } from "@/components/common/label"
import { useToast } from "@/hooks/use-toast"

interface LoginFormProps {
  redirect?: string
}

/**
 * 登录表单组件
 * 支持邮箱密码登录和密钥登录两种方式
 */
export function LoginForm({ redirect = '/overview' }: LoginFormProps) {
  const [activeTab, setActiveTab] = useState<'email' | 'secret'>('email')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [secret, setSecret] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const { login, loginByPassword } = useAuth()
  const router = useRouter()
  const { toast } = useToast()

  const handleEmailLogin = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!email.trim()) {
      toast({ title: '错误', description: '请输入邮箱', variant: 'destructive' })
      return
    }
    if (!password.trim()) {
      toast({ title: '错误', description: '请输入密码', variant: 'destructive' })
      return
    }

    setIsLoading(true)
    try {
      await loginByPassword(email, password)
      router.push(redirect)
    } catch (error) {
      toast({
        title: '登录失败',
        description: error instanceof Error ? error.message : '邮箱或密码错误',
        variant: 'destructive'
      })
    } finally {
      setIsLoading(false)
    }
  }

  const handleSecretLogin = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!secret.trim()) {
      toast({ title: '错误', description: '请输入密钥', variant: 'destructive' })
      return
    }

    setIsLoading(true)
    try {
      await login(secret)
      router.push(redirect)
    } catch (error) {
      toast({
        title: '登录失败',
        description: error instanceof Error ? error.message : '密钥无效',
        variant: 'destructive'
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="space-y-4">
      {/* Tab 切换 */}
      <div className="flex border-b">
        <button
          type="button"
          className={`flex-1 pb-2 text-sm font-medium border-b-2 transition-colors ${
            activeTab === 'email'
              ? 'border-primary text-primary'
              : 'border-transparent text-muted-foreground hover:text-foreground'
          }`}
          onClick={() => setActiveTab('email')}
        >
          邮箱登录
        </button>
        <button
          type="button"
          className={`flex-1 pb-2 text-sm font-medium border-b-2 transition-colors ${
            activeTab === 'secret'
              ? 'border-primary text-primary'
              : 'border-transparent text-muted-foreground hover:text-foreground'
          }`}
          onClick={() => setActiveTab('secret')}
        >
          密钥登录
        </button>
      </div>

      {/* 邮箱密码登录 */}
      {activeTab === 'email' && (
        <form onSubmit={handleEmailLogin} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">邮箱</Label>
            <Input
              id="email"
              type="email"
              placeholder="请输入邮箱"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              disabled={isLoading}
              autoComplete="email"
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="password">密码</Label>
            <Input
              id="password"
              type="password"
              placeholder="请输入密码"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={isLoading}
              autoComplete="current-password"
            />
          </div>
          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? '登录中...' : '登录'}
          </Button>
        </form>
      )}

      {/* 密钥登录 */}
      {activeTab === 'secret' && (
        <form onSubmit={handleSecretLogin} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="secret">密钥</Label>
            <Input
              id="secret"
              type="password"
              placeholder="请输入您的密钥"
              value={secret}
              onChange={(e) => setSecret(e.target.value)}
              disabled={isLoading}
              autoComplete="off"
            />
          </div>
          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? '登录中...' : '登录'}
          </Button>
        </form>
      )}
    </div>
  )
}
