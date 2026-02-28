# Contributing to SkyPath

Thank you for your interest in contributing to SkyPath! This document provides guidelines and instructions for contributing.

## Code of Conduct

Be respectful, inclusive, and professional in all interactions.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/skypath.git
   cd skypath
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/skypath.git
   ```

## Development Setup

### Prerequisites
- Java 11 or higher
- Git

### Build & Test
```bash
cd src
javac -d ../out/production/SkyPath \
  entities/*.java enums/*.java \
  repositories/*.java utilities/*.java \
  services/*.java Main.java

cd ..
java -cp out/production/SkyPath Main
```

## Making Changes

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the code style:
   - Use meaningful variable names
   - Add JavaDoc comments for public methods
   - Keep methods focused and concise
   - Follow SOLID principles

3. **Commit with clear messages**:
   ```bash
   git commit -m "feat: add new flight validation logic"
   ```

4. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Create a Pull Request** on GitHub

## Commit Message Format

Use conventional commits:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation
- `test:` - Test additions/updates
- `refactor:` - Code refactoring
- `perf:` - Performance improvements

Example: `feat: add caching for frequent searches`

## Testing

- All changes must pass existing tests
- Add tests for new features
- Run the full test suite before submitting PR

```bash
java -cp out/production/SkyPath Main
```

Expected: All 9 tests pass (8 functional + 1 concurrent)

## Code Style Guidelines

### Java Conventions
- Class names: `PascalCase`
- Method names: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Use meaningful names over brevity
- Prefer explicit over implicit

### Documentation
- Add JavaDoc comments to public classes and methods
- Include examples in complex methods
- Document exceptions that can be thrown
- Keep comments updated with code changes

### Thread Safety
- Use immutable objects where possible
- Prefer `ConcurrentHashMap` over `HashMap`
- Document thread-safety guarantees
- Use locks consistently and correctly

## Pull Request Process

1. **Update documentation** if needed
2. **Run all tests** locally
3. **Keep commits clean** (logical grouping)
4. **Reference issues** in PR description: "Fixes #123"
5. **Write clear PR title and description**
6. **Respond to review feedback** promptly

## Reporting Issues

Use GitHub Issues to report:
- **Bugs** - Provide steps to reproduce
- **Feature Requests** - Describe use case
- **Questions** - Ask in Discussions tab

Include:
- Java version
- Error messages/stack traces
- Steps to reproduce
- Expected vs actual behavior

## Areas for Contribution

- ✅ REST API wrapper
- ✅ Web frontend
- ✅ Database integration
- ✅ Performance optimization
- ✅ Additional test cases
- ✅ Documentation improvements
- ✅ Docker configuration

## Questions?

- Open an issue for questions
- Check existing issues for similar topics
- Review documentation in README.md

---

**Thank you for contributing to SkyPath! 🚀**

