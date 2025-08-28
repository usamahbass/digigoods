# Test Execution Guide - Digigoods API

## Environment Setup Requirements

### Prerequisites
1. **Java 21** or higher installed and configured
2. **JAVA_HOME** environment variable set correctly
3. **PostgreSQL 15** or higher (for integration tests)
4. **Docker** and **Docker Compose** (alternative approach)

### Environment Verification Commands

```bash
# Check Java installation
java -version
javac -version

# Check JAVA_HOME
echo $JAVA_HOME  # Linux/Mac
echo %JAVA_HOME% # Windows

# Verify Maven wrapper
./mvnw --version  # Linux/Mac
.\mvnw --version  # Windows
```

## Test Execution Commands

### 1. Unit Tests Only
```bash
# Run all unit tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ProductServiceTest

# Run specific test method
./mvnw test -Dtest=ProductServiceTest#givenValidSearchTerm_whenSearchingProductsByName_thenReturnMatchingProducts
```

### 2. Integration Tests
```bash
# Run all tests including integration tests
./mvnw verify

# Run specific integration test
./mvnw test -Dtest=ProductControllerIntegrationTest
```

### 3. Coverage Report Generation
```bash
# Generate test coverage report
./mvnw clean test jacoco:report

# View coverage report
# Open: target/site/jacoco/index.html in browser
```

### 4. Full Build with Tests
```bash
# Complete build including tests and coverage
./mvnw clean verify jacoco:report
```

## Docker-Based Testing (Alternative)

If local Java environment has issues, use Docker:

```bash
# Build and run tests in Docker
docker build -t digigoods-test .
docker run --rm digigoods-test ./mvnw test

# Or use docker-compose for full environment
docker-compose up --build
```

## Expected Test Results

### New Test Classes Added
1. **ProductServiceTest** - Enhanced with search functionality tests
2. **ProductControllerIntegrationTest** - Enhanced with search endpoint tests

### Test Coverage Expectations

#### ProductService Search Tests
- ✅ `givenValidSearchTerm_whenSearchingProductsByName_thenReturnMatchingProducts`
- ✅ `givenNonMatchingSearchTerm_whenSearchingProductsByName_thenReturnEmptyList`
- ✅ `givenSearchTermWithWhitespace_whenSearchingProductsByName_thenTrimWhitespace`
- ✅ `givenNullSearchTerm_whenSearchingProductsByName_thenThrowIllegalArgumentException`
- ✅ `givenEmptySearchTerm_whenSearchingProductsByName_thenThrowIllegalArgumentException`
- ✅ `givenWhitespaceOnlySearchTerm_whenSearchingProductsByName_thenThrowIllegalArgumentException`

#### ProductController Integration Tests
- ✅ `givenValidSearchTerm_whenSearchProducts_thenReturnMatchingProducts`
- ✅ `givenNonMatchingSearchTerm_whenSearchProducts_thenReturnEmptyList`
- ✅ `givenSearchTermMatchingMultiple_whenSearchProducts_thenReturnAllMatching`
- ✅ `givenLowercaseSearchTerm_whenSearchProducts_thenReturnMatchingProducts`
- ✅ `givenMissingSearchParameter_whenSearchProducts_thenReturnBadRequest`

### Coverage Metrics Expected
- **ProductService**: +15-20% line coverage improvement
- **ProductController**: +10-15% line coverage improvement
- **Overall Project**: +5-8% total coverage improvement

## Troubleshooting

### Common Issues

#### 1. JAVA_HOME Not Set
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

# Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

#### 2. Maven Wrapper Permissions (Linux/Mac)
```bash
chmod +x mvnw
```

#### 3. Database Connection Issues
```bash
# Start PostgreSQL with Docker
docker run --name postgres-test -e POSTGRES_DB=digigoods -e POSTGRES_USER=digigoods -e POSTGRES_PASSWORD=digigoods -p 5432:5432 -d postgres:15-alpine
```

#### 4. Port Conflicts
```bash
# Check if port 5432 is in use
netstat -an | grep 5432

# Kill process using port (if needed)
# Windows: taskkill /F /PID <pid>
# Linux/Mac: kill -9 <pid>
```

## Test Validation Checklist

### ✅ Code Quality Checks
- [ ] All tests follow Given-When-Then naming convention
- [ ] All tests have @DisplayName annotations
- [ ] Tests use @Nested classes for organization
- [ ] Proper use of AAA (Arrange-Act-Assert) pattern
- [ ] AssertJ assertions used throughout
- [ ] Mock interactions properly verified

### ✅ Functionality Checks
- [ ] Search endpoint returns correct HTTP status codes
- [ ] Case-insensitive search works correctly
- [ ] Empty search results handled properly
- [ ] Input validation throws appropriate exceptions
- [ ] Integration tests use proper Spring Boot annotations

### ✅ Coverage Checks
- [ ] New search functionality has >90% line coverage
- [ ] Edge cases and error scenarios covered
- [ ] Both unit and integration tests present
- [ ] No regression in existing test coverage

## Files Modified/Created

### Implementation Files
1. `src/main/java/com/example/digigoods/repository/ProductRepository.java`
2. `src/main/java/com/example/digigoods/service/ProductService.java`
3. `src/main/java/com/example/digigoods/controller/ProductController.java`

### Test Files
1. `src/test/java/com/example/digigoods/service/ProductServiceTest.java`
2. `src/test/java/com/example/digigoods/controller/ProductControllerIntegrationTest.java`

### Documentation Files
1. `.augment-guidelines.md`
2. `HANDS-ON.md` (enhanced)
3. `task_observations.md`
4. `task_notes.txt`
5. `test_execution_guide.md` (this file)

## Next Steps After Environment Setup

1. **Run Tests**: Execute the test commands above
2. **Generate Coverage**: Create JaCoCo coverage reports
3. **Validate Results**: Verify all tests pass and coverage improves
4. **Document Results**: Update observations with actual coverage metrics
5. **Refine Guidelines**: Based on test results, enhance guidelines further

## Success Criteria

- ✅ All new tests pass
- ✅ No regression in existing tests
- ✅ Coverage improvement of 5-8% overall
- ✅ Generated code follows all established guidelines
- ✅ Integration tests work with real database
- ✅ Search functionality works as expected
